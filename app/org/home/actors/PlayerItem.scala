package org.home.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.home.actors.messages.StateEvent
import org.home.models.ItemState
import org.home.utils.ItemType
import play.api.libs.json.Json

object PlayerItem {
  def props(state: ItemState): Props = {
    state.itemType match {
      case ItemType.SHIP => Props(new Ship(state))
      case ItemType.BASE => Props(new Factory(state))
    }
  }
}

class BasicPlayerItem(var initState: ItemState) extends Actor with ActorLogging {
  def state: GenericState = {
    initState
  }

  override def receive: Receive = {
    case StateEvent => sender ! state
  }
}

class Ship(initState: ItemState) extends BasicPlayerItem(initState) {
}

class Factory(initState: ItemState) extends BasicPlayerItem(initState) {
}


