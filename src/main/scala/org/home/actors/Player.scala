package org.home.actors

import akka.actor.Actor
import akka.event.Logging

class Player extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case "test" ⇒ log.info("received test")
    case x      ⇒ log.info("received unknown message: " + x )
  }
}

