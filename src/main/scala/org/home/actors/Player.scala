package org.home.actors

import akka.actor.{ActorLogging, Actor}

class Player extends Actor with ActorLogging{
  def receive = {
    case "test" => log.info("received test")
    case x      => log.info("received unknown message: " + x )
  }
}

