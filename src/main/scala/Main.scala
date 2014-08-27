import akka.actor.{Props, ActorSystem}
import org.home.actors.Player

object Main extends App {

  val system = ActorSystem("MainSystem")
  val a1 = system.actorOf(Props[Player], name = "first")

  a1 ! "test"
  a1 ! "something else"

  case class Message(id: String, value: String)
  a1 ! Message("1", "value for this message")

  system.shutdown()
}



