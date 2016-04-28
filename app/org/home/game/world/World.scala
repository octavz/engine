package org.home.game.world

import javax.inject.Inject

import com.badlogic.ashley.core.{Entity, Family, PooledEngine}
import org.home.messages._
import org.home.dto.PlayerDTO
import org.home.game.components._
import org.home.game.systems.{SectorMovementSystem, StateSystem}
import org.home.models._
import org.home.models.universe._
import org.home.utils.{ Randomizer, Vector3D}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import play.api.Logger
import org.home.models.actions.PlayerAction
import org.home.services.UniverseService
import org.home.utils.AshleyScalaModule._

class World @Inject()(universeService: UniverseService) {

  val engine = new PooledEngine()
  engine.addSystem(new SectorMovementSystem)
  engine.addSystem(new StateSystem(universeService))
  var universe: FullUniverse = _
  val forceRestart = true
  //  val generator = actorSystem.actorOf(Generator.props(), name = "generator")

  def start(): Future[String] = {
    Logger.info("Starting new universe.")
    //    actorSystem.scheduler.schedule(20.seconds, 10.second){}
    universeService.loadUniverse(forceRestart) map {
      case res@FullUniverse(u, all) =>
        universe = res
        all foreach engine.addEntity
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

  private def randomSector() = Randomizer.someSector(universe.universe).id

  def registerUser(login: String, password: String, scenario: Int): Future[Entity] = {
    val newUser = UserModel(id = Randomizer.nextId, login = login, password = password, name = login, startSector = randomSector())
    val newPlayer = new Entity()
    newPlayer.add(UserComponent(newUser))
    newPlayer.add(LocationComponent(newUser.startSector, Vector3D.create))
    universeService.registerUser(newPlayer) map createPlayer
  }

  import scala.collection.JavaConversions._

  private def playerBySession(sessionId: String): Option[Entity] = {
    engine.getEntitiesFor(Family.one(classOf[PlayerComponent]).get()).find(_.component[PlayerComponent].sessionId == sessionId)
  }

  def stateForSession(sessionId: String): Entity = {
    playerBySession(sessionId) match {
      case Some(ent) => ent
      case _ => throw new Exception(s"User with session: $sessionId not found.")
    }
  }

  def turn(ticEvent: TicEvent): Unit = {
    //engine.update(ticEvent.currentTime)
  }

  def saveUniverse(): Future[Boolean] = universeService.saveUniverse(universe.universe)

  def getPlayer(id: String): Future[Option[PlayerDTO]] =
    universeService.stateForPlayer(id) map {
      _.map { ps =>
        val model = ps.component[UserComponent].data
        PlayerDTO(id = model.id, name = model.name)
      }
    }

  def performAction(sessionId: String, action: PlayerAction): Future[Any] =
    universeService.findSession(sessionId) map {
      case Some(session) =>
        playerBySession(sessionId) match {
          case Some(e) =>
            e.component[QueueComponent].content += action
          case _ => throw new Exception(s"Found session $sessionId but could not find the entity.")
        }
      case _ => throw new Exception(s"User with session: ${sessionId} not found.")
    }


}

