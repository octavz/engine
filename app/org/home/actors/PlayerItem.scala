package org.home.actors

import akka.actor.{Props, Actor, ActorLogging}
import org.home.utils.PlayerItemTypes

object PlayerItem {
  def props(id: String, owner: String, name: String, itemType: Int, props: Map[String, String]): Props = {
    itemType match {
      case PlayerItemTypes.SHIP => Props(new Ship(id, name, owner, props))
      case PlayerItemTypes.FACTORY => Props(new Factory(id, name, owner, props))
    }
  }
}

class Ship(id: String, owner: String, name: String, props: Map[String, String]) extends Actor with ActorLogging {
  override def receive: Receive = ???
}

class Factory(id: String, owner: String, name: String, props: Map[String, String]) extends Actor with ActorLogging {
  override def receive: Receive = ???
}


