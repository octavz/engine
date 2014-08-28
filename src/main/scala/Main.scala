import akka.actor.{Props, ActorSystem}
import org.home.actors._
import org.home.actors.mesages._

object Main extends App {

  val system = ActorSystem("MainSystem")
  val generator = system.actorOf(Props[Generator], name = "generator")
  val env = system.actorOf(Props(classOf[Env], generator), name = "environment")

  env ! Start

}



