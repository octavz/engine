package org.home.actors

import akka.actor._
import org.home.actors.messages._
import org.home.components.model.UserModel
import org.home.utils.Randomizer._
import play.api.Logger._
import akka.pattern._
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

object Player {
  def props(user: UserModel): Props = Props(new Player(user))
}

class Player(user: UserModel) extends Actor with ActorLogging {
  val items: ListBuffer[String] = ListBuffer.empty

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

  def state: String = {
    //return state here
    val childrenState = context.children.toList.map(_ ? State)
    val s = Future.sequence(childrenState) map (_.mkString(","))
    s"""{"items" : [$s]}"""
  }


  def receive = {
    case NewPlayerItem(itemType, props) =>
      val rep = newItem(itemType, props) match {
        case Some(id) => id
        case _ => Error
      }
      sender ! rep
    case Info => sender ! user
    case State => sender ! state
    case x => log.info("Player received unknown message: " + x)
  }

}

