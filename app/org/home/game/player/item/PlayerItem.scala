package org.home.game.player.item

import akka.actor.{Actor, ActorLogging}
import org.home.messages.{StateEvent, TicEvent, TurnEvent}
import org.home.models.ItemState

abstract class PlayerItem(var state: ItemState) extends Actor with ActorLogging {

  def updateTurnState(time: Long): ItemState

  def turn(time: Long): Unit = {
    log.info(s"Item ${state.id} received tic... $time")
    updateTurnState(time)
  }

  override def receive: Receive = {
    case StateEvent => sender ! state
    case TicEvent(time) => turn(time)
  }
}
