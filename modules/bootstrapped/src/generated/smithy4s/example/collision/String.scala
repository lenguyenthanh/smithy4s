package smithy4s.example.collision

import smithy4s.Newtype
import smithy4s.Schema
import smithy4s.ShapeId
import smithy4s.schema.Schema.bijection
import smithy4s.schema.Schema.string

object String extends Newtype[java.lang.String] {
  val underlyingSchema: Schema[java.lang.String] = string
  .withId(ShapeId("smithy4s.example.collision", "String"))

  implicit val schema: Schema[String] = bijection(underlyingSchema, asBijection)
}
