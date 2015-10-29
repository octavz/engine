package org.home.config

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.util.Timeout
import org.home.actors.Env
import org.home.actors.messages.{Tic, Start}
import org.home.components.RepositoryComponentRedis
import org.home.models.universe.UniverseService
import akka.pattern.ask
import play.api.Logger
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Bootstrap @Inject()(val system: ActorSystem) {

  val universeService = new UniverseService with RepositoryComponentRedis

  implicit val askTimeout = Timeout(5.second)

  var time = 0

  def newTic() = {
    time = time + 1
    Tic(time)
  }

  system.actorOf(Env.props(universeService, forceRestart = false), name = "environment") ? Start map {
    _ =>
      Logger.info("Getting environment")
      val ref = Await.result(system.actorSelection("user/environment").resolveOne(1.second), 1.second)
      system.scheduler.schedule(1.seconds, 5.second)(ref ! newTic())
      Logger.info("Environment loaded.")
  }

}
