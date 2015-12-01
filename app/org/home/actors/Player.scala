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
import org.home.utils.ActionType
import org.home.models.EnvState
import org.home.models.PlayerAction
import org.home.utils.ActionDuration
import org.home.utils.Ops

object Player {
  def props(state: PlayerState): Props = Props(new Player(state))
}

class Player(var state: PlayerState) extends Actor with ActorLogging {
  val items: ListBuffer[String] = ListBuffer.empty
  implicit val askTimeout = Timeout(2.second)
  var currentTime: Long = 0

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

  def restore(playerState: PlayerState): Unit = {
    state = playerState
    playerState.items.foreach {
      s ⇒
        context.actorOf(PlayerItem.props(s), name = s.id)
        items += s.id
    }
  }

  def turn(time: Long): Unit = {
    currentTime = time
  }

  def move(itemId: String, newPos: SectorPosition): Unit = {
    //state = state.modify(_.items.eachWhere(_.id == itemId).location.sectorPosition).setTo(newPos)
    state.items.find(s ⇒ s.id == itemId) match {
      case Some(item) ⇒
        val distance = Ops.dist(item.location.sectorPosition, newPos)
        val time = Math.floor(distance * item.speed).toLong
        state.qu += PlayerAction(ActionType.MOVE_SECTOR, currentTime, currentTime + time, itemId, Some(newPos.toString))
      case _ ⇒ throw new Exception(s"Cannot find item $itemId")
    }
  }

  def performAction[T](a: PlayerActionEvent[T]): Unit = {
    a.action match {
      case MoveInSectorAction(itemId, to) => move(itemId, to)
    }

  }

  def receive: Unit = {
    case NewPlayerItemEvent(itemType, props, location) ⇒
      val rep = newItem(itemType, props, location) match {
        case Some(id) ⇒ id
        case _ ⇒ ErrorEvent
      }
      sender ! rep
    case InfoEvent ⇒ sender ! state.owner
    case StateEvent ⇒ sender ! state
    case a@PlayerActionEvent(sessionId, data) ⇒ performAction(a)
    case t@TicEvent(time) ⇒
      turn(time)
      log.info(s"${state.owner.name} received tic...$time")
    case x ⇒ log.info("Player received unknown message: " + x)
  }

}

