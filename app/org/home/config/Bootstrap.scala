package org.home.config

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.util.Timeout
import org.home.game.world.World
import org.home.messages.{StartEvent, TicEvent}
import org.home.repositories.RepositoryComponentRedis
import akka.pattern.ask
import org.home.services.UniverseService
import play.api.Logger

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Bootstrap @Inject() ( system: ActorSystem, environment: World) {

  val universeService = new UniverseService with RepositoryComponentRedis

  implicit val askTimeout = Timeout(5.second)

  var time = 0L

  def newTic(): TicEvent = {
    time = time + 1
    TicEvent(time)
  }

  environment.start() map { _ =>
    system.scheduler.schedule(1.seconds, 5.second)(environment.turn(newTic()))
    Logger.info("Environment loaded.")
  }



}

