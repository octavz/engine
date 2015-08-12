package org.home.controllers

import javax.ws.rs.{PathParam, QueryParam}


import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.wordnik.swagger.annotations._
import org.home.actors.Env
import org.home.actors.messages.{LoginUser, _}
import org.home.components.RepositoryComponentRedis
import org.home.models._
import org.home.models.universe._
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
    system.scheduler.schedule(1.seconds, 100.second, ref, Tic)
    ref
  }

  implicit val askTimeout = Timeout(5.second)

  @ApiOperation(value = "Start", notes = "Start or reset universe",
    response = classOf[String], httpMethod = "POST", nickname = "start")
  def start(
             @ApiParam(value = "createNew", defaultValue = "false") @PathParam("createNew") createNew: Boolean) =
    Action.async {
      implicit request =>
        response {
          system.actorOf(Env.props(universeService, forceRestart = createNew), name = "environment") ? Start flatMap {
            _ =>
              env ? SaveUniverse map {
                ok =>
                  if (ok.toString.toBoolean) StringResponse("Done")
                  else throw new RuntimeException("Saving failed")
              }
          }
        }
    }

  @ApiOperation(value = "GetUniverse", notes = "Gets current universe", response = classOf[String], httpMethod = "GET", nickname = "index")
  def index = Action.async {
    implicit request =>
      simpleResponse {
        env ? GetUniverse map {
          case u: Universe => Ok(Universe.toJson(u.sectors))
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
        env ? RegisterUser(login, password, scenario) map {
          case (session: String, ps: PlayerState) =>
            Ok(Json.toJson(ps)).withHeaders("Authorization" -> session)
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
        env ? LoginUser(login, password) map {
          case (session: String, ps: PlayerState) =>
            Ok(Json.toJson(ps)).withHeaders("Authorization" -> session)
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
        env ? State(request.sessionId) map {
          case Right(s: PlayerState) => s
          case x => throw new RuntimeException(s"Unknown message: ${x.toString}")
        }
      }
  }
}