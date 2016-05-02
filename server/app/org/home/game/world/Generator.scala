package org.home.game.world


import org.home.messages._
import akka.actor._

import org.home.utils.Randomizer._

object Generator{
  def props(): Props = Props(new Generator)
}

class Generator extends Actor with ActorLogging {

  def newEvent(): RandomEvent = {
    RandomEvent(eventId = nextId, newTimeSpan)
  }

  def receive: Receive = {
    case GenNewEvent => sender ! newEvent()
    case x => log.info(x.toString)
  }

}
