package org.home.game.systems

import javax.inject.{Inject, Singleton}

import com.badlogic.ashley.core.{ComponentMapper, Entity, Family}
import com.badlogic.ashley.systems.IteratingSystem
import org.home.game.components.StateComponent
import org.home.services.MainService
import play.api.Logger
import org.home.utils.AshleyScalaModule._

import scala.concurrent._
import duration._
import ExecutionContext.Implicits.global

@Singleton
class StateSystem @Inject()(service: MainService) extends IteratingSystem(Family.all(classOf[StateComponent]).get()) {

  override def processEntity(entity: Entity, deltaTime: Float): Unit = {
    //      Logger.info(s"Saving ${entity.asJson()}")
    service.persistEntity(entity)
  }

}
