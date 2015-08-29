package org.home.models

import org.home.models.universe._
import play.api.libs.json._

object JsonFormats {
  implicit val fmtObjectSize = Json.format[ObjectSize]
  implicit val fmtSectorPosition = Json.format[SectorPosition]
  implicit val fmtLocation  = Json.format[UniverseLocation]
  implicit val fmtSectorObject  = Json.format[SectorObject]
  implicit val fmtUserModel = Json.format[UserModel]
  implicit val fmtUserSession = Json.format[UserSession]
  implicit val fmtPlayerItemState =  Json.format[ItemState]
  implicit val fmtPlayerAction = Json.format[PlayerAction]
  implicit val fmtPlayerState =  Json.format[PlayerState]
}
