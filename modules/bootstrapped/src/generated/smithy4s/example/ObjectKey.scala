package smithy4s.example

import alloy.UuidFormat
import java.util.UUID
import smithy4s.Newtype
import smithy4s.Schema
import smithy4s.ShapeId
import smithy4s.schema.Schema.bijection
import smithy4s.schema.Schema.uuid

object ObjectKey extends Newtype[UUID] {
  val underlyingSchema: Schema[UUID] = uuid
  .withId(ShapeId("smithy4s.example", "ObjectKey"))
  .addHints(
    UuidFormat(),
  )

  implicit val schema: Schema[ObjectKey] = bijection(underlyingSchema, asBijection)
}
