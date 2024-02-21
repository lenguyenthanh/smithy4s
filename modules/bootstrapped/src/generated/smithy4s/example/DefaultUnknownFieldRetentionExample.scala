package smithy4s.example

import smithy4s.Document
import smithy4s.Hints
import smithy4s.Schema
import smithy4s.ShapeId
import smithy4s.ShapeTag
import smithy4s.schema.Schema.document
import smithy4s.schema.Schema.string
import smithy4s.schema.Schema.struct

final case class DefaultUnknownFieldRetentionExample(retainedUnknownFields: Document = smithy4s.Document.nullDoc, foo: Option[String] = None, bar: Option[String] = None)

object DefaultUnknownFieldRetentionExample extends ShapeTag.Companion[DefaultUnknownFieldRetentionExample] {
  val id: ShapeId = ShapeId("smithy4s.example", "DefaultUnknownFieldRetentionExample")

  val hints: Hints = Hints.empty

  implicit val schema: Schema[DefaultUnknownFieldRetentionExample] = struct(
    document.field[DefaultUnknownFieldRetentionExample]("retainedUnknownFields", _.retainedUnknownFields).addHints(alloy.UnknownFieldRetention(), smithy.api.Default(smithy4s.Document.nullDoc)),
    string.optional[DefaultUnknownFieldRetentionExample]("foo", _.foo),
    string.optional[DefaultUnknownFieldRetentionExample]("bar", _.bar),
  ){
    DefaultUnknownFieldRetentionExample.apply
  }.withId(id).addHints(hints)
}
