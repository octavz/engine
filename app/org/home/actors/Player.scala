package org.home.actors

import akka.actor._
import messages._
import _root_.org.home.components._
import org.home.components.model.UserModel

object Player {
  def props(user: UserModel): Props = Props(new Player(user))
}

class Player(user: UserModel) extends Actor with ActorLogging{

  def receive = {
    case Info => sender ! user
    case x      => log.info("received unknown message: " + x )
  }

}

