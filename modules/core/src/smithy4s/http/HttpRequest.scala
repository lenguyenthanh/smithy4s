/*
 *  Copyright 2021-2022 Disney Streaming
 *
 *  Licensed under the Tomorrow Open Source Technology License, Version 1.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     https://disneystreaming.github.io/TOST-1.0.txt
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package smithy4s.http

import smithy4s.kinds._
import smithy4s.codecs._
import smithy4s.capability.Covariant
import smithy4s.codecs.{Encoder => BodyEncoder}
import smithy4s.schema._
import smithy4s.capability.MonadThrowLike

final case class HttpRequest[+A](
    method: HttpMethod,
    uri: HttpUri,
    headers: Map[CaseInsensitive, Seq[String]],
    body: A
) {
  def map[B](f: A => B): HttpRequest[B] =
    HttpRequest(method, uri, headers, f(body))

  def toMetadata: Metadata = Metadata(
    path = uri.pathParams.getOrElse(Map.empty),
    query = uri.queryParams,
    headers = headers
  )

  def addHeaders(headers: Map[CaseInsensitive, Seq[String]]): HttpRequest[A] =
    this.copy(headers = this.headers ++ headers)

  def withContentType(contentType: String): HttpRequest[A] =
    this.copy(headers =
      this.headers + (CaseInsensitive("Content-Type") -> Seq(contentType))
    )
}

object HttpRequest {
  type Encoder[Body, A] = Writer[HttpRequest[Body], HttpRequest[Body], A]
  type Decoder[F[_], Body, A] = Reader[F, HttpRequest[Body], A]

  implicit val reqCovariant: Covariant[HttpRequest] =
    new Covariant[HttpRequest] {
      def map[A, B](req: HttpRequest[A])(f: A => B): HttpRequest[B] = req.map(f)
    }

  object Encoder {
    private def metadataEncoder[Body]: Encoder[Body, Metadata] = {
      (req: HttpRequest[Body], meta: Metadata) =>
        val oldUri = req.uri
        val newUri =
          oldUri.copy(queryParams = oldUri.queryParams ++ meta.query)
        req.addHeaders(meta.headers).copy(uri = newUri)
    }

    private def bodyEncoder[Body](contentType: String): Encoder[Body, Body] = {
      (req: HttpRequest[Body], body: Body) =>
        req.copy(body = body).withContentType(contentType)
    }

    private def fromMetadataEncoderK[Body]
        : PolyFunction[Metadata.Encoder, Encoder[Body, *]] =
      Metadata.Encoder.toWriterK
        .widen[Writer[HttpRequest[Body], Metadata, *]]
        .andThen(Writer.pipeDataK(metadataEncoder[Body]))

    private def fromEntityEncoderK[Body](
        contentType: String
    ): PolyFunction[BodyEncoder[Body, *], Encoder[Body, *]] =
      Writer
        .pipeDataK[HttpRequest[Body], Body](bodyEncoder[Body](contentType))
        .narrow

    def restSchemaCompiler[Body](
        metadataEncoderCompiler: CachedSchemaCompiler[Metadata.Encoder],
        bodyEncoderCompiler: CachedSchemaCompiler[BodyEncoder[Body, *]],
        contentType: String
    ): CachedSchemaCompiler[Encoder[Body, *]] = {
      val metadataCompiler =
        metadataEncoderCompiler.mapK(fromMetadataEncoderK[Body])
      val bodyCompiler =
        bodyEncoderCompiler.mapK(fromEntityEncoderK[Body](contentType))
      HttpRestSchema.combineWriterCompilers(metadataCompiler, bodyCompiler)
    }

    def fromHttpEndpoint[Body, I](
        httpEndpoint: HttpEndpoint[I]
    ): Encoder[Body, I] = new Encoder[Body, I] {
      def write(request: HttpRequest[Body], input: I): HttpRequest[Body] = {
        val path = httpEndpoint.path(input)
        val staticQueries = httpEndpoint.staticQueryParams
        val oldUri = request.uri
        val newUri =
          oldUri.copy(path = oldUri.path ++ path, queryParams = staticQueries)
        val method = httpEndpoint.method
        request.copy(method = method, uri = newUri)
      }
    }
  }

  object Decoder {

    def restSchemaCompiler[F[_]: MonadThrowLike, Body](
        metadataDecoderCompiler: CachedSchemaCompiler[Metadata.Decoder],
        entityDecoderCompiler: CachedSchemaCompiler[Reader[F, Body, *]]
    ): CachedSchemaCompiler[Decoder[F, Body, *]] = {
      val restMetadataCompiler: CachedSchemaCompiler[Decoder[F, Body, *]] =
        metadataDecoderCompiler.mapK(
          Metadata.Decoder.toReaderK
            .andThen(
              extractMetadata[F](MonadThrowLike.liftEitherK[F, MetadataError])
            )
        )

      val bodyMetadataCompiler: CachedSchemaCompiler[Decoder[F, Body, *]] =
        entityDecoderCompiler.mapK { extractBody[F, Body] }

      HttpRestSchema.combineReaderCompilers[F, HttpRequest[Body]](
        restMetadataCompiler,
        bodyMetadataCompiler
      )
    }

    private def extractMetadata[F[_]](
        liftToF: PolyFunction[Either[MetadataError, *], F]
    ): PolyFunction[Metadata.Reader, Decoder[F, Any, *]] =
      Reader
        .in[Either[MetadataError, *]]
        .composeK((_: HttpRequest[Any]).toMetadata)
        .andThen(Reader.liftPolyFunction(liftToF))

    private def extractBody[F[_], Body]
        : PolyFunction[Reader[F, Body, *], Decoder[F, Body, *]] =
      Reader.in[F].composeK(_.body)

  }

}
