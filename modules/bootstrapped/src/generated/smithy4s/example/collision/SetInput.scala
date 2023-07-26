package smithy4s.example.collision

import smithy.api.Input
import smithy.api.Required
import smithy4s.Schema
import smithy4s.ShapeId
import smithy4s.ShapeTag
import smithy4s.schema.FieldLens
import smithy4s.schema.Schema.struct

final case class SetInput(set: Set[String])
object SetInput extends ShapeTag.Companion[SetInput] {

  val set: FieldLens[SetInput, Set[String]] = MySet.underlyingSchema.required[SetInput]("set", _.set, n => c => c.copy(set = n)).addHints(Required())

  implicit val schema: Schema[SetInput] = struct(
    set,
  ){
    SetInput.apply
  }
  .withId(ShapeId("smithy4s.example.collision", "SetInput"))
  .addHints(
    Input(),
  )
}
