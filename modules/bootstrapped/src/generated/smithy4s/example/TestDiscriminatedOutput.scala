package smithy4s.example

import smithy4s.Hints
import smithy4s.Schema
import smithy4s.ShapeId
import smithy4s.ShapeTag
import smithy4s.schema.Schema.struct

final case class TestDiscriminatedOutput(data: Option[PayloadData] = None)
object TestDiscriminatedOutput extends ShapeTag.Companion[TestDiscriminatedOutput] {
  val id: ShapeId = ShapeId("smithy4s.example", "TestDiscriminatedOutput")

  val hints: Hints = Hints.empty

  val data = PayloadData.schema.optional[TestDiscriminatedOutput]("data", _.data).addHints(smithy.api.HttpPayload())

  implicit val schema: Schema[TestDiscriminatedOutput] = struct(
    data,
  ){
    TestDiscriminatedOutput.apply
  }.withId(id).addHints(hints)
}
