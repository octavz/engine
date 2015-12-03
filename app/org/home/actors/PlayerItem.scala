package org.home.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.home.actors.messages.{TicEvent, MoveInSectorAction, StateEvent}
import org.home.models.{PlayerAction, ItemState}
import org.home.utils._
import com.softwaremill.quicklens._

object PlayerItem {
  def props(state: ItemState): Props = {
    state.itemType match {
      case ItemType.SHIP => Props(new Ship(state))
      case ItemType.BASE => Props(new Factory(state))
    }
  }
}

abstract class BasicPlayerItem(var state: ItemState) extends Actor with ActorLogging {
  var currentTime: Long = 0

  def updateTurnState(): Unit

  def turn(time: Long): Unit = {
    log.info(s"Item ${state.id} received tic... $time")
    currentTime = time
    updateTurnState()
  }

  override def receive: Receive = {
    case StateEvent => sender ! state
    case TicEvent(time) => turn(time)
  }
}

class Ship(shipState: ItemState) extends BasicPlayerItem(shipState) {

  def registerMoveAction(a: MoveInSectorAction): Unit = {
    val newQu = state.qu.filterNot(_.actionType == ActionType.MOVE_SECTOR)
    newQu += PlayerAction(id = Randomizer.nextId
      , actionType = ActionType.MOVE_SECTOR
      , createdOn = currentTime
      , lastModified = currentTime
      , target = Some(state.id)
      , data = Some(a.to.toString))
    state.qu.clear()
    state.qu ++= newQu
  }

  override def receive: Receive = {
    case a: MoveInSectorAction => registerMoveAction(a)
  }

  def removeActionFromQu(id: String): Unit = state.qu ++= state.qu.filterNot(_.id == id)

  protected def performMove() =
    state.qu.find(_.actionType == ActionType.MOVE_SECTOR) match {
      case Some(action) =>
        val serFinalPosition = action.data.getOrElse(throw new RuntimeException("No data for move action"))
        val finalPosition = Vector3D.fromString[Long](serFinalPosition)
        val newPos = Ops.getNextPoint(state.location.sectorPosition, finalPosition)
        state = state.modify(_.location.sectorPosition).setTo(newPos)
        if (newPos == finalPosition) removeActionFromQu(action.id)
      case _ => throw new RuntimeException("Move not allowed, there is no such action")
    }

  override def updateTurnState(): Unit = {
    state.qu.foreach {
      case _: PlayerAction => performMove()
      case _ =>
    }
  }
}

class Factory(factoryState: ItemState) extends BasicPlayerItem(factoryState) {
  override def updateTurnState(): Unit = ???
}


