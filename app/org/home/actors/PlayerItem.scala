package org.home.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.home.actors.messages.{TicEvent, MoveInSectorAction, StateEvent}
import org.home.models.{PlayerAction, ItemState}
import org.home.utils.{Vector3D, ActionType, Ops, ItemType}
import play.api.libs.json.Json

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
  }

  override def receive: Receive = {
    case StateEvent => sender ! state
    case TicEvent(time) => turn(time)
  }
}

class Ship(initState: ItemState) extends BasicPlayerItem(initState) {

  def registerMove(a: MoveInSectorAction): Unit = {
    val newQu = state.qu.filterNot(_.actionType == ActionType.MOVE_SECTOR)
    newQu += PlayerAction(actionType = ActionType.MOVE_SECTOR
      , createdOn = currentTime
      , lastModified = currentTime
      , target = Some(state.id)
      , data = Some(a.to.toString))
    state.qu.clear()
    state.qu ++= newQu
  }

  override def receive: Receive = {
    case a: MoveInSectorAction => registerMove(a)
  }

  import  Ops._

  protected def performMove(lastModified: Long, serFinalPosition: String) = {
    val finalPosition = Vector3D.fromString[Long](serFinalPosition)
    val v0 = Vector3D(state.location.sectorPosition.x, state.location.sectorPosition.y, state.location.sectorPosition.z)
    val v1 = Vector3D(finalPosition.x, finalPosition.y, finalPosition.y)
    val dir = Vector3D(v1.x - v0.x, v1.y - v0.y, v1.z - v0.z)
    def x(t: Long): Long = v0.x - t * dir.x
    def y(t: Long): Long = v0.y - t * dir.y
    def z(t: Long): Long = v0.z - t * dir.z
    val newPos = Vector3D(x(1), y(1), z(1))
  }

  override def updateTurnState(): Unit = {
    state.qu.foreach {
      case a@PlayerAction(ActionType.MOVE_SECTOR, createdOn, lastModified, target, data) =>
        performMove(lastModified, data.getOrElse(throw new Exception("Final position is not found")))
      case _ =>
    }
  }
}

class Factory(initState: ItemState) extends BasicPlayerItem(initState) {
  override def updateTurnState(): Unit = ???
}


