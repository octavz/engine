package org.home.actors

import _root_.org.home.components._
import akka.actor._
import akka.pattern._
import akka.util.Timeout
import org.home.actors.messages._
import org.home.components.model._
import org.home.components.model.universe._
import org.home.utils.Randomizer
import org.home.utils.Randomizer._
import play.api.Logger

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.collection.immutable.Queue
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
    context.system.scheduler.schedule(20.seconds, 10.second, generator, GenNew)
  }

  def loginUser(login: String, password: String): Future[(UserSession, UserModel)] = {
    val f = for {
      user <- repository.findUserByLoginAndEmail(login, password).map(_.getOrElse(throw new Exception("User not found")))
      session <- repository.createSession(UserSession(user.id, nextId))
      playerState <- repository.getPlayerState(user.id).map(_.getOrElse(PlayerState.empty(user))) //if no state then create a new one
    } yield {
        //if cant finds it creates an actor
        context.actorSelection(s"$login").resolveOne(duration) recover {
          case _ =>
            val ref = context.actorOf(Player.props(playerState), name = user.id)
            println(s"User actor created at ${ref.path}")
            ref
        } map {
          a =>
            println(s"Created or found actor at: ${a.path}")
            //create player
            players += user.id
            (session, user)
        }
      }
    f.flatMap(identity)
  }

  def getRandomSector(): Sector = {
    val nodes = universe.sectors.nodes.map(_.value.asInstanceOf[Sector])
    val rand = Randomizer.newInt(0, nodes.size)
    nodes.drop(rand).head
  }

  def registerUser(login: String, password: String): Future[Either[String, UserSession]] = {
    val u = UserModel(id = login, login = login, password = password, name = login)
    val f = repository.registerUser(u) flatMap {
      userModel =>
        //create player
        context.actorOf(
          Player.props(
            state = PlayerState(
              owner = userModel
              , qu = Queue.empty[Int]
              , startSector = getRandomSector().id
              , itemsState = List.empty))
          , name = u.id)
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

  def dispatchAsk(msg: Any): Future[Either[String, List[PlayerState]]] = {
    println("Asking children for state")
    if (players.isEmpty) Future.successful(Right(List.empty))
    else {
      val children = players.map(p => context.actorSelection(s"$p"))
      val f = Future.sequence(children.map { c =>
        (c ? msg) map {
          case e: Either[_, _] => e
        }
      }).map { lst =>
        Right(lst.map {
          case Left(err) => throw new RuntimeException(err.toString)
          case Right(s : PlayerState) => s
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
