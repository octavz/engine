package org.home.components.model

import play.api.libs.json._

object JsonFormats {
  implicit val fmtUserModel = Json.writes[UserModel]
  implicit val fmtUserSession = Json.writes[UserSession]

}
