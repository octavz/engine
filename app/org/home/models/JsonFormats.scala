package org.home.models

import org.home.dto.{PlayerActionDTO, PlayerDTO}
import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.home.models.universe._
import org.home.utils.Vector3D
import play.api.libs.json._
import org.home.actors.messages.MoveInSectorAction

import scala.reflect.ClassTag

object JsonFormats {
  implicit def vectorFormat[T <: AnyVal : Format : ClassTag]: Format[Vector3D[T]] =
    ((__ \ "x").format[T] ~
      (__ \ "y").format[T] ~
      (__ \ "z").format[T]) (Vector3D.apply[T], unlift(Vector3D.unapply[T]))

  implicit val fmtObjectSize = Json.format[ObjectSize]
  implicit val fmtLocation = Json.format[UniverseLocation]
  implicit val fmtSectorObject = Json.format[SectorObject]
  implicit val fmtUserModel = Json.format[UserModel]
  implicit val fmtUserSession = Json.format[UserSession]

  implicit val fmtPlayerAction = Json.format[PlayerAction]

  implicit val fmtPlayerItemState = Json.format[ItemState]
  implicit val fmtPlayerResState = Json.format[ResState]

  implicit val fmtPlayerState = Json.format[PlayerState]
  implicit val fmtPlayerDTO = Json.format[PlayerDTO]
  implicit val fmtPlayerActionDTO = Json.format[PlayerActionDTO]
  implicit val readsMoveInSectorAction = Json.reads[MoveInSectorAction]

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

}
