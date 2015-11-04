package org.home.actors

import org.home.components._
import akka.actor._
import akka.pattern._
import akka.util.Timeout
import org.home.actors.messages._
import org.home.dto.PlayerDTO
import org.home.models._
import org.home.models.universe._
import org.home.utils.Randomizer
import org.home.utils.Randomizer._

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import play.api.Logger

object Env {
  def props(universeService: UniverseService, forceRestart: Boolean): Props =
    Props(new Env(universeService, forceRestart) with RepositoryComponentRedis)
}

class Env(universeService: UniverseService, forceRestart: Boolean) extends Actor with ActorLogging {
  this: RepositoryComponent =>

  val duration = 2.second

  var universe: FullUniverse = _

  implicit val askTimeout = Timeout(2.second)
  val generator = context.actorOf(Generator.props(), name = "generator")
  val sessions = ListBuffer.empty[UserSession]
  var players: ListBuffer[PlayerState] = ListBuffer.empty

  def start() = {
    log.info("Starting new universe.")
    context.system.scheduler.schedule(20.seconds, 10.second, generator, GenNewEvent)
    init() map {
      u =>
        universe = u
        Logger.info("Universe finished loading....")
    }
  }

  def init(): Future[FullUniverse] = universeService.loadUniverse(forceRestart) map {
    case res@FullUniverse(u, all, sess) =>
      all foreach {
        ps =>
          val ref = context.actorOf(Player.props(ps), name = ps.owner.id)
          players += ps
          Logger.info(s"Player created at ${ref.path}")
      }
      sess.foreach(sessions.+=)
      res
  }

  def loginUser(login: String, password: String): Future[(String, PlayerState)] = {
    val f = for {
      ps <- repository
        .findByLoginAndEmail(login, password)
        .map(_.getOrElse(throw new Exception("User not found")))
      session <- repository.createSession(UserSession(ps.owner.id, nextId))
    } yield {
      //if can't finds it creates an actor
      context.actorSelection(s"${ps.owner.id}").resolveOne(duration) recover {
        case _ =>
          throw new Exception(s"Player actor ${ps.owner.id} not found!!")
      } map {
        a =>
          Logger.info(s"Player found at ${a.path}")
          //save session
          sessions += session
          (session.sessionId, ps)
      }
    }
    f.flatMap(identity)
  }

  def registerUser(login: String, password: String, scenario: Int): Future[(String, PlayerState)] = {
    val newUser = UserModel(id = Randomizer.nextId, login = login, password = password, name = login)
    val newState = PlayerState.newPlayer(newUser, scenario, universe.universe)
    repository.registerPlayer(newState) flatMap {
      _ =>
        val ref = context.actorOf(Player.props(newState), name = newState.owner.id)
        players += newState
        Logger.info(s"Player registered at ${ref.path}")
        loginUser(newUser.login, newUser.password)
    }
  }

  def stateForSession(sessionId: String): Future[PlayerState] =
    sessions.find(_.sessionId == sessionId) match {
      case Some(session) =>
        context.actorSelection(s"${session.userId}") ? StateEvent map {
          case s: PlayerState => s
          case m => throw new RuntimeException(s"Message unknowns $m")
        }
      case _ => throw new Exception(s"User with session: $sessionId not found.")
    }

  def turn(time: Long) = {
    Logger.info("Asking children to end turn")
    if (players.nonEmpty) {
      val all = players.map(p => context.actorSelection(s"${p.owner.id}"))
      all.foreach(_ ! TicEvent(time))
    }
  }

  def saveUniverse(): Future[Boolean] = universeService.saveUniverse(universe.universe)

  def shutdown() = {
    log.info("shutdown")
    context.system.shutdown()
  }

  def getPlayer(id: String): Future[Option[PlayerDTO]] =
    repository.stateForPlayer(id) map {
      _.map(ps => PlayerDTO(id = ps.owner.id, name = ps.owner.name))
    }

  def receive = {
    case StartEvent => start().pipeTo(sender())
    case LoginUserEvent(login, pass) => loginUser(login, pass).pipeTo(sender())
    case RegisterUserEvent(login, pass, scenario) => registerUser(login, pass, scenario).pipeTo(sender())
    case StateEvent(session) => stateForSession(session.getOrElse("No session sent")).pipeTo(sender())
    case SaveUniverseEvent => saveUniverse().pipeTo(sender())
    case GetUniverseEvent => Future.successful(universe).pipeTo(sender())
    case ShutdownEvent => shutdown()
    case TicEvent(time) => turn(time)
    case GetPlayerEvent(id) => getPlayer(id).pipeTo(sender())
    case x => log.info("Env received unknown message: " + x)
  }

}
