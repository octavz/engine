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
import org.home.utils.ActionType
import org.home.models.PlayerAction
import org.home.utils.ActionDuration
import org.home.utils.Ops

object Player {
  def props(state: PlayerState): Props = Props(new Player(state))
}

class Player(var state: PlayerState) extends Actor with ActorLogging {
  implicit val askTimeout = Timeout(2.second)
  var currentTime: Long = 0
  val duration = 2.second

  def newItem(itemType: Int, props: Map[String, String], location: UniverseLocation): Option[String] = {
    try {
      val newItemId = nextId
      val item = ItemState(
        id = newItemId
        , itemType = itemType
        , name = newRoman()
        , props = Map.empty
        , location = location
        , qu = emptyActionQu
      )
      context.actorOf(PlayerItem.props(item), name = nextId)
      state.items += item
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
    }
  }

  def turn(time: Long): Unit = {
    log.info(s"${state.owner.name} received tic...$time")
    currentTime = time
  }

  def move(action: MoveInSectorAction): Unit = {
    //state = state.modify(_.items.eachWhere(_.id == itemId).location.sectorPosition).setTo(newPos)
    state.items.find(s ⇒ s.id == action.itemId) match {
      case Some(item) ⇒
        context.actorSelection(s"${item.id}").resolveOne(duration) flatMap { actorItem =>
          actorItem ? action
        }
      //        val distance = Ops.dist(item.location.sectorPosition, newPos)
      //        val time = Math.floor(distance * item.speed).toLong
      //        state.qu += PlayerAction(ActionType.MOVE_SECTOR, currentTime, currentTime + time, itemId, Some(newPos.toString))
      case _ ⇒ throw new Exception(s"Cannot find item ${action.itemId}")
    }
  }

  def receive = {
    case NewPlayerItemEvent(itemType, props, location) ⇒
      val rep = newItem(itemType, props, location) match {
        case Some(id) ⇒ id
        case _ ⇒ ErrorEvent
      }
      sender ! rep
    case InfoEvent ⇒ sender ! state.owner
    case StateEvent ⇒ sender ! state
    case a:MoveInSectorAction ⇒ move(a)
    case t@TicEvent(time) ⇒ turn(time)
    case x ⇒ log.info("Player received unknown message: " + x)
  }

}

