package org.home.components.model

import org.home.components.model.universe._
import play.api.libs.json._

object JsonFormats {
  implicit val fmtUserModel = Json.format[UserModel]
  implicit val fmtUserSession = Json.format[UserSession]
  implicit val fmtPlayerItemState =  Json.format[PlayerItemState]
  implicit val fmtPlayerState =  Json.format[PlayerState]
}
