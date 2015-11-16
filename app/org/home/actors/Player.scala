package org.home.actors

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import org.home.actors.messages._
import org.home.models.universe.SectorPosition
import org.home.utils.Randomizer._
import play.api.Logger._

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import org.home.models._
import com.softwaremill.quicklens._

object Player {
  def props(state: PlayerState): Props = Props(new Player(state))
}

class Player(var state: PlayerState) extends Actor with ActorLogging {
  val items: ListBuffer[String] = ListBuffer.empty
  implicit val askTimeout = Timeout(2.second)

  def newItem(itemType: Int, props: Map[String, String], location: UniverseLocation): Option[String] = {
    try {
      val newItemId = nextId
      context.actorOf(PlayerItem.props(
        ItemState(
          id = newItemId, itemType = itemType, name = newRoman(), props = Map.empty, location = location
        )
      ), name = nextId)
      items += newItemId
      Some(newItemId)
    }
    catch {
      case e: Throwable ⇒
        logger.error("newItem", e)
        None
    }
  }

  def restore(playerState: PlayerState) = {
    state = playerState
    playerState.items.foreach {
      s ⇒
        context.actorOf(PlayerItem.props(s), name = s.id)
        items += s.id
    }
  }

  def turn(time: Long) = {

  }

  def move(itemId: String, newPos: SectorPosition) = {
    state = state.modify(_.items.eachWhere(_.id == itemId).location.sectorPosition).setTo(newPos)
  }

  def receive = {
    case NewPlayerItemEvent(itemType, props, location) ⇒
      val rep = newItem(itemType, props, location) match {
        case Some(id) ⇒ id
        case _        ⇒ ErrorEvent
      }
      sender ! rep
    case InfoEvent ⇒ sender ! state.owner
    case StateEvent ⇒ sender ! state
    case MoveInSectorEvent(itemId, SectorPosition(x, y, z)) ⇒

    case t @ TicEvent(time) ⇒
      turn(time)
      log.info(s"${state.owner.name} received tic...$time")
    case x ⇒ log.info("Player received unknown message: " + x)
  }

}

