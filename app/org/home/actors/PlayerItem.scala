package org.home.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.home.actors.messages.State
import org.home.components.model.PlayerItemState
import org.home.utils.PlayerItemTypes
import play.api.libs.json.Json

object PlayerItem {
  def props(state: PlayerItemState): Props = {
    state.itemType match {
      case PlayerItemTypes.SHIP => Props(new Ship(state))
      case PlayerItemTypes.FACTORY => Props(new Factory(state))
    }
  }
}

class BasicPlayerItem(var initState: PlayerItemState) extends Actor with ActorLogging {
  def state: GenericState = {
    initState
  }

  override def receive: Receive = {
    case State => sender ! state
  }
}

class Ship(initState: PlayerItemState) extends BasicPlayerItem(initState) {
}

class Factory(initState: PlayerItemState) extends BasicPlayerItem(initState) {
}


