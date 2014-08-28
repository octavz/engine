package org.home.actors

import akka.actor._
import scala.concurrent.duration._
import org.home.actors.mesages._
import scala.concurrent.ExecutionContext.Implicits.global

class Env(generator: ActorRef) extends Actor with ActorLogging{

  def start() = {
    log.info("started")
    context.system.scheduler.schedule(1 milli, 1 second, generator, GenNew)
  }

  def shutdown() = {
    log.info("shutdown")
    context.system.shutdown()
  }

  def receive = {
    case Start => start()
    case Shutdown => shutdown()
    case x => log.info("received unknown message: " + x)
  }

}
