package org.home.models

import org.home.models.universe._
import play.api.libs.json._

object JsonFormats {
  implicit val fmtUserModel = Json.format[UserModel]
  implicit val fmtUserSession = Json.format[UserSession]
  implicit val fmtPlayerItemState =  Json.format[ItemState]
  implicit val fmtPlayerAction = Json.format[PlayerAction]
  implicit val fmtPlayerState =  Json.format[PlayerState]
}
