package org.home.actors

import _root_.org.home.components._
import akka.actor._
import akka.pattern._
import akka.util.Timeout
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
  implicit val askTimeout = Timeout(2.second)
  val generator = context.actorOf(Generator.props(), name = "generator")
  val players: ListBuffer[String] = ListBuffer.empty

  def start() = {
    log.info("started")
    context.system.scheduler.schedule(20.seconds, 1.second, generator, GenNew)
  }

  def loginUser(login: String, password: String): Future[Either[String, (UserSession, UserModel)]] = {
    val f = for {
      user <- repository.findUserByLoginAndEmail(login, password)
      session <- repository.createSession(UserSession(user.getOrElse(throw new Exception("User not found")).id, nextId))
    } yield {
        if (user.isEmpty) Future.successful(Left("No user found"))
        else {
          val u = user.get
          //if cant finds it creates an actor
          context.actorSelection(s"$login").resolveOne(duration) recover {
            case _ =>
              val ref = context.actorOf(Player.props(user.get), name = u.id)
              println(s"User actor created at ${ref.path}")
              ref
          } map {
            a =>
              println(s"Created or found actor at: ${a.path}")
              //create player
              players += u.id
              Right(session, u)
          }
        }
      }
    f.flatMap(identity).recover { case ex: Throwable => Left(ex.getMessage) }
  }

  def registerUser(login: String, password: String): Future[Either[String, UserSession]] = {
    val u = UserModel(id = login, login = login, password = password, name = login)
    val f = repository.registerUser(u) flatMap {
      userModel =>
        //create player
        val ref = context.actorOf(Player.props(user = userModel), name = u.id)
        players += login
        //login user
        repository.createSession(UserSession(u.id, nextId)) map {
          session => Right(session)
        }
    }

    f recover {
      case e: Throwable =>
        Logger.logger.error("register", e)
        Left(e.getMessage)
    }
  }

  def dispatchAsk(msg: Any): Future[Either[String, List[String]]] = {
    println("Asking children for state")
    if (players.isEmpty) Future.successful(Right(List.empty))
    else {
      val children = players.map(p => context.actorSelection(s"$p"))
      val f = Future.sequence(children.map { c =>
        (c ? msg) map {
          case e: Either[String, String] => e
        }
      }).map { lst =>
        Right(lst.map {
          case Left(err) => throw new RuntimeException(err)
          case Right(s) => s
        }.toList)
      }

      f recover {
        case e: Throwable => Left(e.getMessage)
      }
    }
  }

  def turn() = {
    println("Asking children to end turn")
    if (players.nonEmpty) {
      val all = players.map(p => context.actorSelection(s"$p"))
      all.foreach(_ ! Tic)
    }
  }

  def shutdown() = {
    log.info("shutdown")
    context.system.shutdown()
  }

  def receive = {
    case Start => start()
    case LoginUser(login, pass) => loginUser(login, pass).pipeTo(sender())
    case RegisterUser(login, pass) => registerUser(login, pass).pipeTo(sender())
    case State => dispatchAsk(State).pipeTo(sender())
    case Shutdown => shutdown()
    case Tic => turn()
    case x => log.info("Env received unknown message: " + x)
  }

}
