package org.home.game.systems

import com.badlogic.ashley.core.{Entity, Family}
import com.badlogic.ashley.systems.IteratingSystem
import org.home.game.components.StateComponent

import scala.concurrent.Future
import  scala.concurrent.ExecutionContext.Implicits.global

class StateSystem extends IteratingSystem(Family.all(classOf[StateComponent]).get()) {


  override def processEntity(entity: Entity, deltaTime: Float): Unit = Future {
    //save
  }


}
