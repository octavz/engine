package controllers

import javax.ws.rs.QueryParam

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import org.home.actors.messages.{LoginUser, _}
import org.home.components.model.{UserModel, JsonFormats}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.Play.current
import scala.concurrent._
import ExecutionContext.Implicits.global

import scala.concurrent._
import scala.concurrent.duration._
import play.api.libs.concurrent.Akka
import JsonFormats._
import com.wordnik.swagger.annotations._

@Api(value = "/main", description = "Operations")
object MainController extends Controller {

  lazy val env = {
    println("Getting environment")
    Await.result(Akka.system.actorSelection("user/environment").resolveOne(1.second), 1.second)
  }

  implicit val askTimeout = Timeout(2.second)

  @ApiOperation(value = "Index", notes = "Index", response = classOf[UserModel], httpMethod = "GET", nickname = "index")
  def index = Action.async {
    val f = env ? LoginUser("cucu", "bucu")
    f.flatMap {
      case Some(r) =>
        (r.asInstanceOf[ActorRef] ? Info).map(a => Ok(Json.toJson(a.asInstanceOf[UserModel])))
      case _ => Future.successful(BadRequest("no user found"))
    }
  }

  @ApiOperation(value = "Register", notes = "Register", response = classOf[String], httpMethod = "POST", nickname = "register")
  def register(
                @ApiParam(value = "login") @QueryParam("login") login: String,
                @ApiParam(value = "password") @QueryParam("password") password: String) = Action.async {
    val f = env ? RegisterUser(login, password)
    f.map {
      case NoError => Ok("ok")
      case Error => BadRequest("not ok")
    }
  }

  @ApiOperation(value = "Login", notes = "Login", response = classOf[String], httpMethod = "POST", nickname = "login")
  def login(
                @ApiParam(value = "login") @QueryParam("login") login: String,
                @ApiParam(value = "password") @QueryParam("password") password: String) = Action.async {
    val f = env ? LoginUser(login, password)
    f.map {
      case session: String => Ok(session)
      case _ => BadRequest("not ok")
    }
  }
}
