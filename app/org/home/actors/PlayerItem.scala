package org.home.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.home.actors.messages.State
import org.home.utils.PlayerItemTypes
import play.api.libs.json.Json

object PlayerItem {
  def props(id: String, owner: String, name: String, itemType: Int, props: Map[String, String]): Props = {
    itemType match {
      case PlayerItemTypes.SHIP => Props(new Ship(id, name, owner, props))
      case PlayerItemTypes.FACTORY => Props(new Factory(id, name, owner, props))
    }
  }
}

class Ship(id: String, owner: String, name: String, props: Map[String, String]) extends Actor with ActorLogging {
  def state = {
    s"""{"id": "$id", "owner":"$owner", "name":"$name" , "props":"${Json.toJson(props).toString()}"  }"""
  }

  override def receive: Receive = {
    case State => sender ! state
  }
}

class Factory(id: String, owner: String, name: String, props: Map[String, String]) extends Actor with ActorLogging {
  def state: Either[String, String] = {
    try {
      Right( s"""{"id": "$id", "owner":"$owner", "name":"$name" , "props":"${Json.toJson(props).toString()}"  }""")
    }
    catch {
      case e: Throwable => Left(e.getMessage)
    }
  }

  override def receive: Receive = {
    case State => sender ! state
  }
}


