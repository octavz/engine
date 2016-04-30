package org.home.game.world

import javax.inject._

import com.badlogic.ashley.core.{Entity, Family, PooledEngine}
import org.home.messages._
import org.home.dto.PlayerDTO
import org.home.game.components._
import org.home.game.systems.{SectorMovementSystem, StateSystem}
import org.home.models.universe._
import org.home.utils.{Builders, Randomizer, ScenarioType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import play.api.Logger
import org.home.models.actions.PlayerAction
import org.home.services.MainService
import org.home.utils.AshleyScalaModule._

import scala.collection.JavaConversions._

@Singleton
class World @Inject()(service: MainService, stateSystem: StateSystem) {

  private implicit val engine = new PooledEngine()
  engine.addSystem(new SectorMovementSystem)
  engine.addSystem(stateSystem)
  var data: Option[FullUniverse] = None
  val forceRestart = true
  val (defaultLogin, defaultPassword) = ("test", "test")
  //  val generator = actorSystem.actorOf(Generator.props(), name = "generator")

  def start(): Future[Player] = if (data.isEmpty) {
    Logger.info("Starting new universe.")
    //    actorSystem.scheduler.schedule(20.seconds, 10.second){}
    service.loadUniverse(forceRestart) flatMap {
      case res@FullUniverse(u, all) =>
        data = Some(res)
        all foreach engine.addEntity
        Logger.info("Universe finished loading....")
        if (forceRestart) {
          registerUser(defaultLogin, defaultPassword, ScenarioType.NORMAL) flatMap { a =>
            Future.sequence(a.items.map(service.persistEntity) :+ service.persistEntity(a.player)) flatMap { res =>
              loginUser(defaultLogin, defaultPassword)
            }
          }
        } else {
          loginUser(defaultLogin, defaultPassword)
        }
    }
  } else Future.successful {
    engine
      .getEntitiesFor(Family.all(classOf[UserComponent]).get())
      .find(e => e.component[UserComponent].login == defaultLogin)
      .flatMap(e => fullPlayer(e.component[UserComponent].id))
      .getOrElse(throw new Exception(s"No default user: $defaultLogin found"))
  }

  private def playerById(id: String): Option[Entity] = {
    val ents = engine.getEntitiesFor(Family.all(classOf[UserComponent]).get())
    ents.find(_.component[UserComponent].id == id)
  }

  private def fullPlayer(id: String): Option[Player] = playerById(id) map (Player(_, playerItems(id).toSeq))

  private def addPlayer(player: Entity): Entity = {
    playerById(player.component[UserComponent].id) match {
      case Some(e) => engine.removeEntity(e)
      case _ =>
    }
    engine.addEntity(player)
    player
  }

  def users: Seq[Entity] = engine.getEntitiesFor(Family.all(classOf[UserComponent]).get()).toSeq

  def loginUser(login: String, password: String): Future[Player] =
    service.loginUser(login, password) map { sessionComponent =>
      playerById(sessionComponent.session.userId) match {
        case Some(entity) =>
          entity.add(sessionComponent) //attach a session to this guy
          Player(entity, playerItems(sessionComponent.session.userId).toSeq)
        case _ => throw new Exception(s"Engine doesn't contain ${sessionComponent.session.userId}")
      }
    }

  private def randomSector() = Randomizer.someSector(data.get.universe).id

  private def playerItems(playerId: String) = {
    engine.getEntitiesFor(Family.all(classOf[ChildComponent]).get()).filter(_.component[ChildComponent].parent == playerId)
  }

  case class Player(player: Entity, items: Seq[Entity]) {
    def asJson()(implicit excludedComponents: Seq[Class[_]] = Seq.empty): String = {
      val playerJson = player.asJson()(excludedComponents)
      val id = player.component[UserComponent].id
      val childrenJson = playerItems(id).map(_.asJson()).mkString(",")
      s"""{"player": $playerJson, "items": [$childrenJson]}"""
    }
  }

  def registerUser(login: String, password: String, scenario: Int): Future[Player] = {
    val newUser = UserComponent(id = Randomizer.nextId, login = login, password = password, name = login, startSector = randomSector())
    service.registerUser(newUser) map (_ => (Player.apply _).tupled(Builders.createNewPlayer(newUser)))
    //this only adds the login to the indexes so we can find is when logging in
    //the actual saving of the player is done using the StateService
  }

  import scala.collection.JavaConversions._

  private def playerBySession(sessionId: String): Option[Entity] = {
    val all = engine.getEntitiesFor(Family.one(classOf[SessionComponent]).get())
    all.find(_.component[SessionComponent].session.sessionId == sessionId)
  }

  def stateForSession(sessionId: String): Future[Player] = Future {
    playerBySession(sessionId) match {
      case Some(ent) => Player(ent, playerItems(ent.component[UserComponent].id).toSeq)
      case _ => throw new Exception(s"User with session: $sessionId not found.")
    }
  }

  def turn(ticEvent: TicEvent): Unit = if (data.isDefined) {
    Logger.info(s"Turn ${ticEvent.currentTime}")
    engine.update(ticEvent.currentTime)
  }

  def saveUniverse(): Future[Boolean] = service.saveUniverse(data.get.universe)

  def getPlayer(id: String): Future[Option[PlayerDTO]] =
    service.stateForPlayer(id) map {
      _.map { ps =>
        val model = ps.component[UserComponent]
        PlayerDTO(id = model.id, name = model.name)
      }
    }

  def performAction(sessionId: String, action: PlayerAction): Future[Any] =
    service.findSession(sessionId) map {
      case Some(session) =>
        playerBySession(sessionId) match {
          case Some(e) =>
            e.component[QueueComponent].content += action
          case _ => throw new Exception(s"Found session $sessionId but could not find the entity.")
        }
      case _ => throw new Exception(s"User with session: $sessionId not found.")
    }


}

