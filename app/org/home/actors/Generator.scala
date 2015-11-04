package org.home.actors


import messages._
import akka.actor._

import org.home.utils.Randomizer._

object Generator{
  def props() = Props(new Generator)
}

class Generator extends Actor with ActorLogging {

  def newEvent(): ActionEvent = {
    GenericEvent(eventId = nextId, newTimeSpan)
  }

  def receive = {
    case GenNewEvent => sender ! newEvent()
    case x => log.info(x.toString)
  }

}
