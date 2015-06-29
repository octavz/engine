package org.home.components.model

import play.api.libs.json._

object JsonFormats {
  implicit val fmtUserModel = Json.format[UserModel]
  implicit val fmtUserSession = Json.format[UserSession]

}
