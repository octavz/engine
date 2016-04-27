package org.home.controllers

import javax.ws.rs.{PathParam, QueryParam}

import akka.actor._
import com.google.inject.Inject
import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState
import com.wordnik.swagger.annotations._
import org.home.messages._
import org.home.repositories.RepositoryComponentRedis
import org.home.dto.{PlayerActionDTO, PlayerDTO}
import org.home.game.world.World
import org.home.models.universe._
import play.api.Logger
import play.api.mvc._

import scala.concurrent._
import ExecutionContext.Implicits.global
import org.home.services.UniverseService
import org.home.utils.ActionType
import com.owlike.genson.defaultGenson._
import org.home.game.components.PlayerComponent
import org.home.utils.AshleyScalaModule._

@Api(value = "/main", description = "Operations")
@javax.inject.Singleton
class MainController @Inject()(system: ActorSystem, env: World) extends Controller {
  val universeService = new UniverseService with RepositoryComponentRedis

  @ApiOperation(value = "Start", notes = "Start or reset universe", response = classOf[String],
    httpMethod = "POST", nickname = "start")
  def start(): Action[AnyContent] =
    Action.async {
      implicit request ⇒
        response {
          env.start()
        }
    }

  @ApiOperation(value = "GetUniverse", notes = "Gets current universe", response = classOf[String],
    httpMethod = "GET", nickname = "index")
  def index: Action[AnyContent] = Action {
    implicit request ⇒
      val ret = Universe.toJson(env.universe.universe.sectors)
      Ok(ret)
  }

  @ApiOperation(value = "GetPlayer", notes = "Gets player public", response = classOf[PlayerDTO],
    httpMethod = "GET", nickname = "getPlayer")
  def getPlayer(@PathParam("id") id: String): Action[AnyContent] = Action.async {
    implicit request ⇒
      response {
        env.getPlayer(id) map {
          case Some(a) ⇒ a match {
            case d: PlayerDTO ⇒ a.asInstanceOf[PlayerDTO]
            case _ ⇒ throw new Exception("No idea what i got")
          }
          case None ⇒ throw new Exception("User not found.")
          case _ ⇒ throw new Exception("No idea what i got")
        }
      }
  }

  @ApiOperation(value = "Register", notes = "Register", response = classOf[PlayerState],
    httpMethod = "POST", nickname = "register")
  def register(
                @ApiParam(value = "login") @QueryParam("login") login: String,
                @ApiParam(value = "password") @QueryParam("password") password: String,
                @ApiParam(value = "scenario", defaultValue = "0") @QueryParam("scenario") scenario: Int
              ): Action[AnyContent] = Action.async {
    implicit request ⇒
      simpleResponse {
        env.registerUser(login, password, scenario) map { ps =>
          val ret = toJson(ps)
          Logger.info(ret)
          Ok(ret).withHeaders("Authorization" → ps.component[PlayerComponent].sessionId.sessionId)
        }
      }
  }

  @ApiOperation(value = "Login", notes = "Login", response = classOf[PlayerState],
    httpMethod = "POST", nickname = "login")
  def login(
             @ApiParam(value = "login") @QueryParam("login") login: String,
             @ApiParam(value = "password") @QueryParam("password") password: String
           ): Action[AnyContent] = Action.async {
    implicit request ⇒
      simpleResponse {
        env.loginUser(login, password) map { ps =>
          val ret = toJson(ps)
          Logger.info(ret)
          Ok(ret).withHeaders("Authorization" → ps.component[PlayerComponent].sessionId.sessionId)
        }
      }
  }

  @ApiOperation(value = "Get state", response = classOf[String], httpMethod = "GET", nickname = "getState")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Authorization", value = "authorization", defaultValue = "",
      required = true, dataType = "string", paramType = "header")
  ))
  def stateForSession: Action[AnyContent] = Action {
    implicit request ⇒
      Ok(toJson(env.stateForSession(request.sessionId.get)))
  }

  @ApiOperation(value = "Create action", response = classOf[Boolean],
    httpMethod = "POST", nickname = "createAction")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Authorization", value = "authorization", required = true,
      dataType = "string", paramType = "header"),
    new ApiImplicitParam(value = "The player action", required = true,
      dataType = "org.home.dto.PlayerActionDTO", paramType = "body")
  ))
  def createAction(): Action[AnyContent] = Action.async {
    implicit request ⇒
      request.body.asJson.map {
        json ⇒
          response {
            val req = fromJson[PlayerActionDTO](json.toString())
            req.action match {
              case ActionType.MOVE_SECTOR ⇒
                val ev = PlayerActionEvent(
                  actionType = req.action
                  , sessionId = request.sessionId.getOrElse(throw new Exception("You must be authorized"))
                  , actionData = req.data, currentTime = 1)
                env.performAction(ev) map {
                  case Right(_) ⇒ true
                  case _ ⇒ false
                }
            }

          }
      }.getOrElse(throw new Exception("Bad Json"))
  }

}

