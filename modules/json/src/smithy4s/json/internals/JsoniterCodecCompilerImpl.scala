package smithy4s.json
package internals

import smithy4s.internals._
import smithy4s.IntEnum
import smithy4s.HintMask
import smithy4s.schema._
import smithy.api._
import alloy._

private[smithy4s] case class JsoniterCodecCompilerImpl(
    maxArity: Int,
    explicitNullEncoding: Boolean,
    sparseCollectionsSupport: Boolean,
    infinitySupport: Boolean,
    hintMask: Option[HintMask]
) extends CachedSchemaCompiler.Impl[JCodec]
    with JsoniterCodecCompiler {

  type Aux[A] = JCodec[A]

  def withMaxArity(max: Int): JsoniterCodecCompiler = copy(maxArity = max)

  def withExplicitNullEncoding(
      explicitNullEncoding: Boolean
  ): JsoniterCodecCompiler =
    copy(explicitNullEncoding = explicitNullEncoding)

  def withHintMask(hintMask: HintMask): JsoniterCodecCompiler =
    copy(hintMask = Some(hintMask))

  def withSparseCollectionsSupport(
      sparseCollectionsSupport: Boolean
  ): JsoniterCodecCompiler =
    copy(sparseCollectionsSupport = sparseCollectionsSupport)

  def withInfinitySupport(infinitySupport: Boolean): JsoniterCodecCompiler =
    copy(infinitySupport = infinitySupport)

  def fromSchema[A](schema: Schema[A], cache: Cache): JCodec[A] = {
    val visitor = new SchemaVisitorJCodec(maxArity, explicitNullEncoding, cache)
    val amendedSchema =
      hintMask
        .map(mask => schema.transformHintsTransitively(mask.apply))
        .getOrElse(schema)
    amendedSchema.compile(visitor)
  }

}

private[smithy4s] object JsoniterCodecCompilerImpl {

  val defaultHintMask: HintMask =
    HintMask(
      JsonName,
      TimestampFormat,
      Discriminated,
      Untagged,
      InputOutput,
      DiscriminatedUnionMember,
      IntEnum,
      Default
    )
  val defaultMaxArity: Int = 1024

  val defaultJsoniterCodecCompiler: JsoniterCodecCompiler =
    JsoniterCodecCompilerImpl(
      maxArity = defaultMaxArity,
      explicitNullEncoding = false,
      infinitySupport = false,
      sparseCollectionsSupport = false,
      hintMask = Some(defaultHintMask)
    )

}