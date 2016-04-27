package org.home.game.world

import javax.inject.Inject

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import com.badlogic.ashley.core.{Entity, Family, PooledEngine}
import org.home.messages._
import org.home.dto.PlayerDTO
import org.home.game.components._
import org.home.game.systems.{SectorMovementSystem, StateSystem}
import org.home.models._
import org.home.models.universe._
import org.home.utils.{ActionType, Randomizer, Vector3D}
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import play.api.Logger
import org.home.models.JsonFormats._
import org.home.services.UniverseService
import org.home.utils.AshleyScalaModule._

class World @Inject()(actorSystem: ActorSystem, universeService: UniverseService, forceRestart: Boolean) {

  val engine = new PooledEngine()
  engine.addSystem(new SectorMovementSystem)
  engine.addSystem(new StateSystem)
  var universe: FullUniverse = _
  //  val generator = actorSystem.actorOf(Generator.props(), name = "generator")

  def start(): Future[String] = {
    Logger.info("Starting new universe.")
    //    actorSystem.scheduler.schedule(20.seconds, 10.second){}
    universeService.loadUniverse(forceRestart) map {
      case res@FullUniverse(u, all) =>
        universe = res
        all foreach {
          player =>
            createPlayer(player)
        }
        Logger.info("Universe finished loading....")
        "ok"
    }
  }

  def createPlayer(player: Entity): Entity = {
    engine.addEntity(player)
    player
  }

  def loginUser(login: String, password: String): Future[Entity] = {
    universeService.loginUser(login, password) map createPlayer
  }

  def randomSector() = Randomizer.someSector(universe.universe).id

  def registerUser(login: String, password: String, scenario: Int): Future[Entity] = {
    val newUser = UserModel(id = Randomizer.nextId, login = login, password = password, name = login, startSector = randomSector())
    val newPlayer = new Entity()
    newPlayer.add(UserComponent(newUser))
    newPlayer.add(LocationComponent(newUser.startSector, Vector3D.create))
    universeService.registerUser(newPlayer) map createPlayer
  }

  import scala.collection.JavaConversions._

  def stateForSession(sessionId: String): Entity = {
    engine.getEntitiesFor(Family.one(classOf[PlayerComponent]).get()).find(_.component[PlayerComponent].sessionId == sessionId) match {
      case Some(ent) => ent
      case _ => throw new Exception(s"User with session: $sessionId not found.")
    }
  }

  def turn(ticEvent: TicEvent): Unit = {
    engine.update(ticEvent.currentTime)
  }

  def saveUniverse(): Future[Boolean] = universeService.saveUniverse(universe.universe)

  def getPlayer(id: String): Future[Option[PlayerDTO]] =
    universeService.stateForPlayer(id) map {
      _.map { ps =>
        val model = ps.component[UserComponent].data
        PlayerDTO(id = model.id, name = model.name)
      }
    }

  def performAction(actionEvent: PlayerActionEvent): Future[Any] =
    universeService.findSession(actionEvent.sessionId) flatMap {
      case Some(session) =>
        actionEvent.actionType match {
          case ActionType.MOVE_SECTOR =>
            val action = Json.parse(actionEvent.actionData).as[MoveInSectorEvent]
            //            actorSystem.actorSelection(s"${session.userId}") ? action
            Future.successful(())
        }
      case _ => throw new Exception(s"User with session: ${actionEvent.sessionId} not found.")
    }

  //  def receive: Receive = {
  //    case StartEvent => start().pipeTo(sender())
  //    case LoginUserEvent(login, pass) => loginUser(login, pass).pipeTo(sender())
  //    case RegisterUserEvent(login, pass, scenario) => registerUser(login, pass, scenario).pipeTo(sender())
  //    case StateEvent(session) => stateForSession(session.getOrElse("No session sent")).pipeTo(sender())
  //    case SaveUniverseEvent => saveUniverse().pipeTo(sender())
  //    case GetUniverseEvent => Future.successful(universe).pipeTo(sender())
  //    case ShutdownEvent => shutdown()
  //    case t@TicEvent(time) => turn(t)
  //    case GetPlayerEvent(id) => getPlayer(id).pipeTo(sender())
  //    case a: PlayerActionEvent => performAction(a).pipeTo(sender())
  //    case x => log.info("Env received unknown message: " + x)
  //  }

}

