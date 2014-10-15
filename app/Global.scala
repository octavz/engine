import akka.actor.Props
import org.home.actors._
import org.home.actors.messages._
import play.api.{Application, GlobalSettings}
import play.api.libs.concurrent.Akka
import play.api.Play.current

/**
 * Created by octav on 15.10.2014.
 */
object Global extends GlobalSettings {

  override def onStart(app: Application) = {
    val generator = Akka.system.actorOf(Props[Generator], name = "generator")
    val env = Akka.system.actorOf(Env.props(generator), name = "environment")
    println(env.path)
    env ! Start
  }

  override def onStop(app: Application): Unit = {
    //env ! Shutdown
  }

}
