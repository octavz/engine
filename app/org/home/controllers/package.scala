package org.home

import play.api.Logger
import play.api.mvc.{Results, AnyContent, Request}
import play.api.mvc.Result
import scala.concurrent.ExecutionContext.Implicits.global
import com.owlike.genson.defaultGenson._

import scala.concurrent.Future

package object controllers extends Results {

  case class ErrorMessage(mesage: String)

  case class StringResponse(value: String)

  def response[T: Manifest](call: ⇒ Future[T])(implicit request: Request[AnyContent]): Future[Result] = {
    val ret = try {
      call.map { r ⇒
        val json = toJson(r)
        Logger.info(json)
        Ok(json)
      }.recover {
        case e: Throwable ⇒
          Logger.error("", e)
          BadRequest(toJson(ErrorMessage(e.getMessage)))
      }
    }
    catch {
      case e: Throwable ⇒
        Future.successful(BadRequest(toJson(ErrorMessage(e.getMessage))))
    }
    ret map {
      r ⇒
        if (r.header.headers.contains("Authorization")) r
        else r.withHeaders("Authorization" → request.sessionId.getOrElse(""))
    }
  }

  def simpleResponse(call: ⇒ Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    val ret = try {
      call.recover {
        case e: Throwable ⇒
          Logger.error("", e)
          BadRequest(toJson(ErrorMessage(e.getMessage)))
      }
    }
    catch {
      case e: Throwable ⇒
        Future.successful(BadRequest(toJson(ErrorMessage(e.getMessage))))
    }
    ret map {
      r ⇒
        (if (r.header.headers.contains("Authorization")) r
        else r.withHeaders("Authorization" → request.sessionId.getOrElse(""))).withHeaders("Content-Type" → "application/json")
    }
  }

  implicit class HeaderExtractor(request: Request[AnyContent]) {

    def sessionId: Option[String] = request.headers.toSimpleMap.get("Authorization")

  }

}
