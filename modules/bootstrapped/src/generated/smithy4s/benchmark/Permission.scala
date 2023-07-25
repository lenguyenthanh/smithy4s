package smithy4s.benchmark

import smithy4s.Hints
import smithy4s.Schema
import smithy4s.ShapeId
import smithy4s.ShapeTag
import smithy4s.schema.Schema.boolean
import smithy4s.schema.Schema.struct

final case class Permission(read: Option[Boolean] = None, write: Option[Boolean] = None, directory: Option[Boolean] = None)
object Permission extends ShapeTag.Companion[Permission] {
  val id: ShapeId = ShapeId("smithy4s.benchmark", "Permission")

  val hints: Hints = Hints.empty

  val read = boolean.optional[Permission]("read", _.read)
  val write = boolean.optional[Permission]("write", _.write)
  val directory = boolean.optional[Permission]("directory", _.directory)

  implicit val schema: Schema[Permission] = struct(
    read,
    write,
    directory,
  ){
    Permission.apply
  }.withId(id).addHints(hints)
}
