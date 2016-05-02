package org.home.config

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.util.Timeout
import org.home.game.world.World
import org.home.messages.TicEvent
import play.api.Logger

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class Bootstrap @Inject()(system: ActorSystem, environment: World) {

  implicit val askTimeout = Timeout(5.second)

  var time = 0L

  def newTic(): TicEvent = {
    time = time + 1
    TicEvent(time)
  }

  system.scheduler.schedule(5.seconds, 5.second)(environment.turn(newTic()))
//  environment.start() map { _ =>
//    Logger.info("Environment loaded.")
//  }


}

