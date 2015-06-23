package org.home.actors

import _root_.org.home.components._
import akka.actor._
import messages._
import org.home.components.model.{UserModel, UserSession}
import org.home.utils.Randomizer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import akka.pattern._

object Env {
  def props(generator: ActorRef): Props = Props(new Env(generator) with RepositoryComponentImpl)
}

class Env(generator: ActorRef) extends Actor with ActorLogging {
  this: RepositoryComponent =>
  val duration = 2.second

  def start() = {
    log.info("started")
    //context.system.scheduler.schedule(1.milli, 1.second, generator, GenNew)
  }

  def login(login: String, password: String): Future[Option[UserSession]] = {

    val f = for {
      user <- repository.findUserByLoginAndEmail(login, password)
      session <- repository.createSession(UserSession(user.getOrElse(throw new Exception("User not found")).id, Randomizer.newId))
    } yield session
    //      {
    //
    //      context.system.actorSelection(s"user/$login").resolveOne(5.seconds) recover {
    //        case _ => context.system.actorOf(Player.props(user.get), name = login)
    //      }
    //}

    f.map(s => Some(s)).recover { case _ => Option.empty[UserSession] }
  }

  def register(login: String, password: String): Future[Boolean] =
    repository.registerUser(UserModel(id = Randomizer.newId, login = login, password = password, name = login))

  def shutdown() = {
    log.info("shutdown")
    context.system.shutdown()
  }

  def receive = {
    case Start => start()
    case LoginUser(l, p) => login(l, p).map {
      case Some(s) => s.sessionId
      case _ => Error
    }.pipeTo(sender())
    case RegisterUser(l, p) => register(l, p).map {
      case true => NoError
      case _ => Error
    }.pipeTo(sender())
    case Shutdown => shutdown()
    case x => log.info("received unknown message: " + x)
  }

}
