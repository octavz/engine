package org.home.actors


import messages._

import akka.actor._

import org.home.utils.Randomizer._

class Generator extends Actor with ActorLogging {

  def newEvent(): ActionEvent = {
    GenericEvent(eventId = newId, newTimeSpan)
  }

  def receive = {
    case GenNew => sender ! newEvent()
    case x => log.info(x.toString)
  }

}
