package org.home.models

import org.home.dto.{PlayerActionDTO, PlayerDTO}
import org.home.models.universe._
import play.api.libs.json._
import org.home.actors.messages.MoveInSectorEvent

object JsonFormats {
  implicit val fmtObjectSize = Json.format[ObjectSize]
  implicit val fmtSectorPosition = Json.format[SectorPosition]
  implicit val fmtLocation  = Json.format[UniverseLocation]
  implicit val fmtSectorObject  = Json.format[SectorObject]
  implicit val fmtUserModel = Json.format[UserModel]
  implicit val fmtUserSession = Json.format[UserSession]
  implicit val fmtPlayerItemState =  Json.format[ItemState]
  implicit val fmtPlayerResState =  Json.format[ResState]
  implicit val fmtPlayerAction = Json.format[PlayerAction]
  implicit val fmtPlayerState =  Json.format[PlayerState]
  implicit val fmtPlayerDTO =  Json.format[PlayerDTO]
  implicit val fmtPlayerActionDTO =  Json.format[PlayerActionDTO]
  implicit val readsMoveInSectorEvent = Json.reads[MoveInSectorEvent]

}
