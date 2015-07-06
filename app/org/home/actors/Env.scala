package org.home.actors

import _root_.org.home.components._
import akka.actor._
import akka.pattern._
import org.home.actors.messages._
import org.home.components.model.{UserModel, UserSession}
import org.home.models.Universe
import org.home.utils.Randomizer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

object Env {
  def props(universe: Universe): Props = Props(new Env(universe) with RepositoryComponentImpl)
}

class Env(universe: Universe) extends Actor with ActorLogging {
  this : RepositoryComponent =>
  val duration = 2.second
  val generator = context.actorOf(Props[Generator], name = "generator")

  def start() = {
    log.info("started")
    println(universe)
    context.system.scheduler.schedule(1.milli, 100000.milli, generator, GenNew)
  }

  def login(login: String, password: String): Future[Option[(UserSession, UserModel)]] = {
    val f = for {
      user <- repository.findUserByLoginAndEmail(login, password)
      session <- repository.createSession(UserSession(user.getOrElse(throw new Exception("User not found")).id, Randomizer.newId))
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

  def register(login: String, password: String): Future[Boolean] =
    repository.registerUser(UserModel(id = Randomizer.newId, login = login, password = password, name = login))

  def shutdown() = {
    log.info("shutdown")
    context.system.shutdown()
  }

  def receive = {
    case Start => start()
    case LoginUser(l, p) => login(l, p).map {
      case Some((session, user)) => (session, user)
      case _ => Error
    }.pipeTo(sender())
    case RegisterUser(l, p) => register(l, p).map {
      case true => NoError
      case _ => Error
    }.pipeTo(sender())
    case Shutdown => shutdown()
    case x => log.info("Env received unknown message: " + x)
  }

}
