package smithy4s.benchmark

import smithy4s.Hints
import smithy4s.Schema
import smithy4s.ShapeId
import smithy4s.ShapeTag
import smithy4s.optics.Lens
import smithy4s.schema.Schema.string
import smithy4s.schema.Schema.struct

final case class Creds(user: Option[String] = None, key: Option[String] = None)
object Creds extends ShapeTag.Companion[Creds] {
  val id: ShapeId = ShapeId("smithy4s.benchmark", "Creds")

  val hints: Hints = Hints.empty

  object Optics {
    val user = Lens[Creds, Option[String]](_.user)(n => a => a.copy(user = n))
    val key = Lens[Creds, Option[String]](_.key)(n => a => a.copy(key = n))
  }

  implicit val schema: Schema[Creds] = struct(
    string.optional[Creds]("user", _.user),
    string.optional[Creds]("key", _.key),
  ){
    Creds.apply
  }.withId(id).addHints(hints)
}
