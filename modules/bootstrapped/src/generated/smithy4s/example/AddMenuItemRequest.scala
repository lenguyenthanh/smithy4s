package smithy4s.example

import smithy4s.Hints
import smithy4s.Schema
import smithy4s.ShapeId
import smithy4s.ShapeTag
import smithy4s.schema.Schema.string
import smithy4s.schema.Schema.struct

final case class AddMenuItemRequest(restaurant: String, menuItem: MenuItem)
object AddMenuItemRequest extends ShapeTag.Companion[AddMenuItemRequest] {
  val id: ShapeId = ShapeId("smithy4s.example", "AddMenuItemRequest")

  val hints: Hints = Hints.empty

  val restaurant = string.required[AddMenuItemRequest]("restaurant", _.restaurant).addHints(smithy.api.HttpLabel(), smithy.api.Required())
  val menuItem = MenuItem.schema.required[AddMenuItemRequest]("menuItem", _.menuItem).addHints(smithy.api.HttpPayload(), smithy.api.Required())

  implicit val schema: Schema[AddMenuItemRequest] = struct(
    restaurant,
    menuItem,
  ){
    AddMenuItemRequest.apply
  }.withId(id).addHints(hints)
}
