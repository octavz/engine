package org.home.controllers

import javax.ws.rs.{PathParam, QueryParam}
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.wordnik.swagger.annotations._
import org.home.actors.messages.{LoginUserEvent, _}
import org.home.components.RepositoryComponentRedis
import org.home.dto.{PlayerActionDTO, PlayerDTO}
import org.home.models._
import org.home.models.universe._
import play.api.Logger
import play.api.libs.concurrent.Akka
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import org.home.models.JsonFormats._
import play.api.Play.current

@Api(value = "/main", description = "Operations")
@javax.inject.Singleton
class MainController @Inject()(system: ActorSystem) extends Controller {
  val universeService = new UniverseService with RepositoryComponentRedis

  lazy val env = {
    println("Getting environment")
    val ref = Await.result(Akka.system.actorSelection("user/environment").resolveOne(1.second), 1.second)
    ref
  }

  implicit val askTimeout = Timeout(5.second)

  @ApiOperation(value = "Start", notes = "Start or reset universe",
    response = classOf[String], httpMethod = "POST", nickname = "start")
  def start() =
    Action.async {
      implicit request =>
        response {
          env ? SaveUniverseEvent map {
            ok =>
              if (ok.toString.toBoolean) StringResponse("Done")
              else throw new RuntimeException("Saving failed")
          }
        }
    }

  @ApiOperation(value = "GetUniverse", notes = "Gets current universe", response = classOf[String], httpMethod = "GET", nickname = "index")
  def index = Action.async {
    implicit request =>
      simpleResponse {
        env ? GetUniverseEvent map {
          case u: FullUniverse =>
            val ret = Universe.toJson(u.universe.sectors)
            Ok(ret)
        }
      }
  }

  @ApiOperation(value = "GetPlayer", notes = "Gets player public", response = classOf[PlayerDTO], httpMethod = "GET", nickname = "getPlayer")
  def getPlayer(@PathParam("id") id: String) = Action.async {
    implicit request =>
      response {
        env ? GetPlayerEvent(id) map {
          case Some(a) => a match {
            case d: PlayerDTO => a.asInstanceOf[PlayerDTO]
            case _ => throw new Exception("No idea what i got")
          }
          case None => throw new Exception("User not found.")
          case _ => throw new Exception("No idea what i got")
        }
      }
  }

  @ApiOperation(value = "Register", notes = "Register", response = classOf[PlayerState], httpMethod = "POST", nickname = "register")
  def register(
                @ApiParam(value = "login") @QueryParam("login") login: String,
                @ApiParam(value = "password") @QueryParam("password") password: String,
                @ApiParam(value = "scenario", defaultValue = "0") @QueryParam("scenario") scenario: Int) = Action.async {
    implicit request =>
      simpleResponse {
        env ? RegisterUserEvent(login, password, scenario) map {
          case (session: String, ps: PlayerState) =>
            val ret = Json.toJson(ps)
            Logger.info(ret.toString())
            Ok(ret).withHeaders("Authorization" -> session)
          case x => throw new RuntimeException(s"Unknown message: ${x.toString}")
        }
      }
  }

  @ApiOperation(value = "Login", notes = "Login", response = classOf[PlayerState], httpMethod = "POST", nickname = "login")
  def login(
             @ApiParam(value = "login") @QueryParam("login") login: String,
             @ApiParam(value = "password") @QueryParam("password") password: String) = Action.async {
    implicit request =>
      simpleResponse {
        env ? LoginUserEvent(login, password) map {
          case (session: String, ps: PlayerState) =>
            val ret = Json.toJson(ps)
            Logger.info(ret.toString())
            Ok(ret).withHeaders("Authorization" -> session)
          case x => throw new RuntimeException(s"Unknown message: ${x.toString}")
        }
      }
  }

  @ApiOperation(value = "Get state", response = classOf[String], httpMethod = "GET", nickname = "getState")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Authorization", value = "authorization", defaultValue = "",
      required = true, dataType = "string", paramType = "header")
  ))
  def stateForSession = Action.async {
    implicit request =>
      response {
        env ? StateEvent(request.sessionId) map {
          case s: PlayerState => s
          case x => throw new RuntimeException(s"Unknown message when expecting state: ${x.toString}")
        }
      }
  }

  @ApiOperation(value = "Create action", response = classOf[Boolean], httpMethod = "POST", nickname = "createAction")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Authorization", value = "authorization", defaultValue = "", required = true, dataType = "string", paramType = "header"),
    new ApiImplicitParam(value = "The player action", required = true, dataType = "org.home.dto.PlayerActionDTO", paramType = "body")
  ))
  def createAction() = Action.async {
    implicit request =>
      request.body.asJson.map {
        json =>
          response {
            env ? PlayerActionEvent(json.as[PlayerActionDTO]) map {
              case Right(_) => true
              case _ => false
            }
          }
      }.getOrElse(throw new Exception("Bad Json"))
  }

}

