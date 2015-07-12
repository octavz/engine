package org.home.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.home.actors.messages.State
import org.home.utils.PlayerItemTypes
import play.api.libs.json.Json

object PlayerItem {
  def props(itemType: Int, state: PlayerItemState): Props = {
    itemType match {
      case PlayerItemTypes.SHIP => Props(new Ship(state))
      case PlayerItemTypes.FACTORY => Props(new Factory(state))
    }
  }
}

case class PlayerItemState(id: String, owner: String, name: String, props: Map[String, String]) extends GenericState {
  implicit val jsonFormat = Json.format[PlayerItemState]
}

class Ship(initState: PlayerItemState) extends Actor with ActorLogging {
  def state: GenericState = {
    initState
  }

  override def receive: Receive = {
    case State => sender ! state
  }
}

class Factory(initState: PlayerItemState) extends Actor with ActorLogging {
  def state: GenericState = {
    initState
  }

  override def receive: Receive = {
    case State => sender ! state
  }
}


