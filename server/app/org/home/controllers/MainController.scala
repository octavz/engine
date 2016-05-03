package org.home.controllers

import javax.ws.rs.{PathParam, QueryParam}

import akka.actor._
import com.google.inject.Inject
import com.wordnik.swagger.annotations._
import org.home.dto.{LoginDTO, PlayerActionDTO, PlayerDTO}
import org.home.game.world.World
import org.home.models.universe._
import play.api.Logger
import play.api.mvc._

import scala.concurrent._
import ExecutionContext.Implicits.global
import org.home.services.MainService
import org.home.utils.Randomizer
import org.home.game.components.{SessionComponent, StateComponent}
import org.home.models.actions.PlayerAction
import org.home.utils.AshleyScalaModule._
import org.home.utils._
import play.api.libs.json.Json

@Api(value = "/main", description = "Operations")
@javax.inject.Singleton
class MainController @Inject()(system: ActorSystem, world: World, service: MainService) extends Controller {
  implicit private val excludedComponents = Seq(classOf[SessionComponent], classOf[StateComponent])
  implicit val fmtLogin = Json.reads[LoginDTO]

  @ApiOperation(value = "Start", notes = "Start or reset universe", response = classOf[String],
    httpMethod = "POST", nickname = "start")
  def start(): Action[AnyContent] = Action.async {
    implicit request ⇒
      asyncCall {
        world.start() map { r =>
          val comp = r.player.component[SessionComponent]
          Ok(r.asJson()).withHeaders("Authorization" → comp.sessionId)
        }
      }
  }

  @ApiOperation(value = "getUniverse", notes = "Gets current universe", response = classOf[String],
    httpMethod = "GET", nickname = "index")
  def index: Action[AnyContent] = Action {
    implicit request ⇒
      val ret = Universe.toJson(world.data.get.universe.sectors)
      Ok(ret)
  }

  @ApiOperation(value = "getAllUsers", notes = "Gets current users", httpMethod = "GET", nickname = "getAllUsers")
  def users: Action[AnyContent] = Action {
    implicit request ⇒
      val all = world.users.map(_.asJson()(Seq.empty)).mkString(",")
      Ok(s"[$all]").withHeaders(jsonHeader)
  }

  @ApiOperation(value = "GetPlayer", notes = "Gets player public", response = classOf[PlayerDTO],
    httpMethod = "GET", nickname = "getPlayer")
  def getPlayer(@PathParam("id") id: String): Action[AnyContent] = Action {
    implicit request ⇒
      call {
        world.getPlayer(id) match {
          case Some(a) ⇒ Ok(a.toJson)
          case None ⇒ throw new Exception("User not found.")
        }
      }
  }

  @ApiOperation(value = "Register", notes = "Register",
    httpMethod = "POST", nickname = "register")
  def register(
                @ApiParam(value = "login") @QueryParam("login") login: String,
                @ApiParam(value = "password") @QueryParam("password") password: String,
                @ApiParam(value = "scenario", defaultValue = "0") @QueryParam("scenario") scenario: Int
              ): Action[AnyContent] = Action.async {
    implicit request ⇒
      asyncCall {
        world.registerUser(login, password, scenario) map { player =>
          Ok(player.asJson())
        }
      }
  }

  @ApiOperation(value = "Login", notes = "Login", response = classOf[world.Player],
    httpMethod = "POST", nickname = "login")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "login request", required = true,
      paramType = "body", dataType = "org.home.dto.LoginDTO")))
  def login(): Action[AnyContent] = Action.async {
    implicit request ⇒
      asyncCall {
        val dto = request.body.asJson.getOrElse(throw new Exception("Wrong json body for login")).as[LoginDTO]
        world.loginUser(dto.login, dto.password) map {
          ps =>
            val ret = ps.asJson()
            Logger.info(ret)
            Ok(ret).withHeaders("Authorization" → ps.player.component[SessionComponent].sessionId)
        }
      }
  }

  @ApiOperation(value = "Get state", response = classOf[String], httpMethod = "GET", nickname = "getState")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Authorization", value = "authorization", defaultValue = "",
      required = true, dataType = "string", paramType = "header")
  ))
  def stateForSession: Action[AnyContent] = Action.async {
    implicit request ⇒
      asyncCall {
        world.stateForSession(request.sessionId) map {
          entity =>
            Ok(entity.asJson())
        }
      }
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
            val now = System.currentTimeMillis()
            val req = fromJson[PlayerActionDTO](json.toString())
            val action = PlayerAction(
              actionType = req.action
              , createdOn = now
              , lastModified = now
              , data = req.data
              , id = Randomizer.newId)
            world.performAction(request.sessionId, action) map {
              case Right(_) ⇒ true
              case _ ⇒ false
            }

          }
      }.getOrElse(throw new Exception("Bad Json"))
  }

}

