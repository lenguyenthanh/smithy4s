package smithy4s.example

import smithy4s.Hints
import smithy4s.Schema
import smithy4s.ShapeId
import smithy4s.ShapeTag
import smithy4s.optics.Lens
import smithy4s.schema.Schema.struct

final case class GetIntEnumOutput(result: EnumResult)
object GetIntEnumOutput extends ShapeTag.Companion[GetIntEnumOutput] {
  val id: ShapeId = ShapeId("smithy4s.example", "GetIntEnumOutput")

  val hints: Hints = Hints(
    smithy.api.Output(),
  )

  object Optics {
    val result = Lens[GetIntEnumOutput, EnumResult](_.result)(n => a => a.copy(result = n))
  }

  implicit val schema: Schema[GetIntEnumOutput] = struct(
    EnumResult.schema.required[GetIntEnumOutput]("result", _.result).addHints(smithy.api.Required()),
  ){
    GetIntEnumOutput.apply
  }.withId(id).addHints(hints)
}
