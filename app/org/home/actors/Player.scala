package org.home.actors

import akka.actor._
import org.home.actors.messages._
import org.home.components.model.UserModel
import org.home.utils.Randomizer._
import play.api.Logger._

import scala.collection.mutable.ListBuffer

object Player {
  def props(user: UserModel): Props = Props(new Player(user))
}

class Player(user: UserModel) extends Actor with ActorLogging {
  var items: ListBuffer[ActorRef] = ListBuffer.empty

  def newItem(itemType: Int, props: Map[String, String]): Option[String] = {
    try {
      val newItemId = newId
      val item = context.actorOf(PlayerItem.props(id = newItemId, owner = user.id, name = newRoman(), itemType = itemType, Map.empty), name = newId)
      items += item
      Some(newItemId)
    } catch {
      case e: Throwable =>
        logger.error("newItem", e)
        None
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
    case x => log.info("Player received unknown message: " + x)
  }

}

