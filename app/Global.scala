import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {

  override def onStart(app: Application) = {
  }

  override def onStop(app: Application): Unit = {
    //env ! Shutdown
  }

}
