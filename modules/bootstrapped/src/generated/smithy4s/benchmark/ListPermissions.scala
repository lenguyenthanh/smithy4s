package smithy4s.benchmark

import smithy4s.Newtype
import smithy4s.Schema
import smithy4s.ShapeId
import smithy4s.schema.Schema.bijection
import smithy4s.schema.Schema.list

object ListPermissions extends Newtype[List[Permission]] {
  val underlyingSchema: Schema[List[Permission]] = list(Permission.schema)
  .withId(ShapeId("smithy4s.benchmark", "ListPermissions"))

  implicit val schema: Schema[ListPermissions] = bijection(underlyingSchema, asBijection)
}
