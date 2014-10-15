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

  def login(login: String, password: String): Option[ActorRef] = Await.result(
    repository.findUserByLoginAndEmail(login, password) flatMap {
      case Some(user) =>
        repository.createSession(UserSession(user.id, Randomizer.newId)) flatMap {
          case true =>
            context.system.actorSelection(s"user/$login").resolveOne(5.seconds) map (Some(_)) recover {
              case _ => Some(context.system.actorOf(Player.props(user), name = login))
            }
          case _ => Future.successful(None)
        }
      case _ => Future.successful(None)
    }
    , duration)

  def register(login: String, password: String): Future[Boolean] =
    repository.registerUser(UserModel(id = Randomizer.newId, login = login, password = password, name = login))

  def shutdown() = {
    log.info("shutdown")
    context.system.shutdown()
  }

  def receive = {
    case Start => start()
    case LoginUser(l, p) => sender ! login(l, p)
    case RegisterUser(l, p) => register(l, p).map {
      case true => NoError
      case _ => Error
    }.pipeTo(sender)
    case Shutdown => shutdown()
    case x => log.info("received unknown message: " + x)
  }

}
