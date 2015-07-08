package org.home.actors

import _root_.org.home.components._
import akka.actor._
import akka.pattern._
import org.home.actors.messages._
import org.home.components.model.{UserModel, UserSession}
import org.home.models.Universe
import org.home.utils.Randomizer._
import play.api.Logger

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

object Env {
  def props(universe: Universe): Props = Props(new Env(universe) with RepositoryComponentImpl)
}

class Env(universe: Universe) extends Actor with ActorLogging {
  this: RepositoryComponent =>
  val duration = 2.second
  val generator = context.actorOf(Generator.props(), name = "generator")
  val players: ListBuffer[ActorRef] = ListBuffer.empty

  def start() = {
    log.info("started")
    context.system.scheduler.schedule(1.milli, 100000.milli, generator, GenNew)
  }

  def loginUser(login: String, password: String): Future[Option[(UserSession, UserModel)]] = {
    val f = for {
      user <- repository.findUserByLoginAndEmail(login, password)
      session <- repository.createSession(UserSession(user.getOrElse(throw new Exception("User not found")).id, newId))
    } yield {
        if (user.isEmpty) Future.successful(Option.empty[(UserSession, UserModel)])
        val u = user.get
        //if cant finds it creates an actor
        context.system.actorSelection(s"user/$login").resolveOne(5.seconds) recover {
          case _ =>
            context.system.actorOf(Player.props(user.get), name = u.id)
        } map {
          a =>
            println(s"Created or found actor at: ${a.path}")
            Some(session, u)
        }
      }
    f.flatMap(identity).recover { case _ => Option.empty[(UserSession, UserModel)] }
  }

  def registerUser(login: String, password: String): Future[Option[UserSession]] = {
    val newUserId = newId
    val f = repository.registerUser(UserModel(id = newUserId, login = login, password = password, name = login)) flatMap {
      userModel =>
        //create player
        val player = context.actorOf(Player.props(user = userModel), name = newUserId)
        players += player
        //login user
        repository.createSession(UserSession(newUserId, newId)) map {
          session =>
            Some(session)
        }
    }

    f recover {
      case e: Throwable =>
        Logger.logger.error("register", e)
        None
    }
  }


  def shutdown() = {
    log.info("shutdown")
    context.system.shutdown()
  }

  def receive = {
    case Start => start()
    case LoginUser(login, pass) => loginUser(login, pass).map {
      case Some((session, user)) => (session, user)
      case _ => Error
    }.pipeTo(sender())
    case RegisterUser(login, pass) => registerUser(login, pass).map {
      case Some(s) => s
      case _ => Error
    }.pipeTo(sender())
    case Shutdown => shutdown()
    case x => log.info("Env received unknown message: " + x)
  }

}
