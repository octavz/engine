package org.home

import play.api.Logger
import play.api.libs.json.{Writes, Json}
import play.api.mvc.{Results, Action, AnyContent, Request}
import play.api.mvc.Result
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

package object controllers extends Results {

  case class ErrorMessage(mesage: String)

  case class StringResponse(value: String)

  implicit val fmtErrror = Json.format[ErrorMessage]
  implicit val fmtStringResponse = Json.format[StringResponse]

  def response[T](call: => Future[T])(implicit request: Request[AnyContent], write: Writes[T]): Future[Result] = {
    val ret = try {
      call.map(r => Ok(Json.toJson(r))).recover {
        case e: Throwable =>
          Logger.logger.error("", e)
          BadRequest(Json.toJson(ErrorMessage(e.getMessage)))
      }
    } catch {
      case e: Throwable =>
        Future.successful(BadRequest(Json.toJson(ErrorMessage(e.getMessage))))
    }
    ret map {
      r =>
        if (r.header.headers.contains("Authorization")) r
        else r.withHeaders("Authorization" -> request.sessionId.getOrElse(""))
    }
  }

  def simpleResponse(call: => Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    val ret = try {
      call.recover {
        case e: Throwable =>
          Logger.logger.error("", e)
          BadRequest(Json.toJson(ErrorMessage(e.getMessage)))
      }
    } catch {
      case e: Throwable =>
        Future.successful(BadRequest(Json.toJson(ErrorMessage(e.getMessage))))
    }
    ret map {
      r =>
        (if (r.header.headers.contains("Authorization")) r
        else r.withHeaders("Authorization" -> request.sessionId.getOrElse(""))).withHeaders("Content-Type" -> "application/json")
    }
  }

  implicit class HeaderExtractor(request: Request[AnyContent]) {

    def sessionId = request.headers.toSimpleMap.get("Authorization")

  }

}
