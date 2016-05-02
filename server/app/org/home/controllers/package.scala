package org.home

import play.api.Logger
import play.api.mvc.{Results, AnyContent, Request}
import play.api.mvc.Result
import scala.concurrent.ExecutionContext.Implicits.global
import org.home.utils._

import scala.concurrent.Future

package object controllers extends Results {

  case class ErrorMessage(mesage: String)

  case class StringResponse(value: String)

  def response[T: Manifest](call: ⇒ Future[T])(implicit request: Request[AnyContent]): Future[Result] = {
    val fRet = try {
      call.map { r ⇒
        val json = r.toJson
        Logger.info(json)
        Ok(json)
      }.recover {
        case e: Throwable ⇒
          Logger.error("", e)
          BadRequest(ErrorMessage(e.getMessage).toJson)
      }
    } catch {
      case e: Throwable ⇒
        Future.successful(BadRequest(ErrorMessage(e.getMessage).toJson))
    }
    fRet map {
      r ⇒
        if (r.header.headers.contains("Authorization")) r
        else r.withHeaders("Authorization" → request.optSessionId.getOrElse(""))
    }
  }

  val jsonHeader: (String, String) = "Content-Type" → "application/json"

  def call(call: ⇒ Result)(implicit request: Request[AnyContent]): Result =
    try {
      val r = call
      (if (r.header.headers.contains("Authorization")) r
      else r.withHeaders("Authorization" → request.optSessionId.getOrElse(""))).withHeaders(jsonHeader)
    } catch {
      case e: Throwable ⇒
        e.printStackTrace()
        BadRequest(ErrorMessage(e.getMessage).toJson)
    }

  def asyncCall(call: ⇒ Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    val ret = try {
      call.recover {
        case e: Throwable ⇒
          Logger.error("", e)
          BadRequest(ErrorMessage(e.getMessage).toJson)
      }
    }
    catch {
      case e: Throwable ⇒
        e.printStackTrace()
        Future.successful(BadRequest(ErrorMessage(e.getMessage).toJson))
    }
    ret map {
      r ⇒
        (if (r.header.headers.contains("Authorization")) r
        else r.withHeaders("Authorization" → request.optSessionId.getOrElse(""))).withHeaders(jsonHeader)
    }
  }

  implicit class HeaderExtractor(request: Request[AnyContent]) {

    def optSessionId: Option[String] = request.headers.toSimpleMap.get("Authorization")

    def sessionId: String = request.headers.toSimpleMap.getOrElse("Authorization", throw new Exception("Not authorized"))

  }

}
