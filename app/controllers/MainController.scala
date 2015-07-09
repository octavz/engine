package controllers

import javax.ws.rs.QueryParam

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.wordnik.swagger.annotations._
import org.home.actors.Env
import org.home.actors.messages.{LoginUser, _}
import org.home.components.model.{UserSession, UserModel}
import org.home.models.Universe
import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import org.home.components.model.JsonFormats._

@Api(value = "/main", description = "Operations")
@javax.inject.Singleton
class MainController @Inject()(system: ActorSystem) extends Controller {
  val universe = Universe.create()
  val environment = system.actorOf(Env.props(universe), name = "environment")
  println(env.path)
  env ! Start

  lazy val env = {
    println("Getting environment")
    Await.result(Akka.system.actorSelection("user/environment").resolveOne(1.second), 1.second)
  }

  implicit val askTimeout = Timeout(2.second)

  @ApiOperation(value = "Index", notes = "Index", response = classOf[UserModel], httpMethod = "GET", nickname = "index")
  def index = Action.async {
    val f = env ? LoginUser("octav@test.com", "123456")
    f.flatMap {
      case Left(err) => Future.successful(BadRequest(err.toString))
      case Right((s, u)) =>
        val user = u.asInstanceOf[UserModel]
        val res = Akka.system.actorSelection(s"user/${user.id}").resolveOne(2.seconds).flatMap {
          actor =>
            Future.successful(Ok(universe.toJson))
          //(actor ? Info).map(a => Ok(Json.toJson(a.asInstanceOf[UserModel])))
        }
        res
      //Future.successful(Ok(s.toString))
    }
  }

  @ApiOperation(value = "Register", notes = "Register", response = classOf[String], httpMethod = "POST", nickname = "register")
  def register(
                @ApiParam(value = "login") @QueryParam("login") login: String,
                @ApiParam(value = "password") @QueryParam("password") password: String) = Action.async {
    val f = env ? RegisterUser(login, password)
    f.map {
      case Right(session) => Ok(Json.toJson(session.asInstanceOf[UserSession]))
      case Left(err) => BadRequest(err.toString)
    }
  }

  @ApiOperation(value = "Login", notes = "Login", response = classOf[String], httpMethod = "POST", nickname = "login")
  def login(
             @ApiParam(value = "login") @QueryParam("login") login: String,
             @ApiParam(value = "password") @QueryParam("password") password: String) = Action.async {
    val f = env ? LoginUser(login, password)
    f.map {
      case e: Either[String, (UserSession, UserModel)] => e match {
        case Right((session, user)) => Ok(Json.toJson(session))
        case Left(err) => BadRequest(err)
      }
      case x => BadRequest(x.toString)
    }
  }

  @ApiOperation(value = "Get state", response = classOf[String], httpMethod = "GET", nickname = "getState")
  def getState = Action.async {
    val f = (env ? State) map {
      case e: Either[String, List[String]] => e match {
        case Left(err) => BadRequest(err)
        case Right(lst) => Ok(lst.mkString("\n-------------------\n"))
      }
    }
    f recover {
      case e: Throwable =>
        Logger.logger.error("getState", e)
        println(e.getMessage)
        BadRequest(e.getMessage)
    }
  }
}
