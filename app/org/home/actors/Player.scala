package org.home.actors

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import org.home.actors.messages._
import org.home.components.model.UserModel
import org.home.utils.Randomizer._
import play.api.Logger
import play.api.Logger._

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object Player {
  def props(user: UserModel): Props = Props(new Player(user))
}

class Player(user: UserModel) extends Actor with ActorLogging {
  val items: ListBuffer[String] = ListBuffer.empty
  implicit val askTimeout = Timeout(2.second)

  def newItem(itemType: Int, props: Map[String, String]): Option[String] = {
    try {
      val newItemId = nextId
      context.actorOf(PlayerItem.props(id = newItemId, owner = user.id, name = newRoman(), itemType = itemType, Map.empty), name = nextId)
      items += newItemId
      Some(newItemId)
    } catch {
      case e: Throwable =>
        logger.error("newItem", e)
        None
    }
  }

  def state: Future[Either[String, String]] = {
    //return state here
    val childrenState = Future.sequence(context.children.toList.map {
      c =>
        val cf = c ? State
        cf map {
          case e: Either[_, _] => e
        }
    })

    val f = childrenState map {
      lst =>
        lst map {
          case Right(s) => s
          case _ => throw new RuntimeException("Failed to get state")
        }
    } map {
      lst =>
        val st = s"""{"id" : ${user.id},"items" : [${lst.mkString(",")}]}"""
        println(st)
        Right(st)
    }

    f recover {
      case e: Throwable =>
        Logger.logger.error("getPlayerState", e)
        Left(e.getMessage)
    }
  }

  def receive = {
    case NewPlayerItem(itemType, props) =>
      val rep = newItem(itemType, props) match {
        case Some(id) => id
        case _ => Error
      }
      sender ! rep
    case Info => sender ! user
    case State => state.pipeTo(sender())
    case x => log.info("Player received unknown message: " + x)
  }

}

