package org.home.game.systems

import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem

import scala.collection.JavaConversions._

abstract class ParallelIteratingSystem(family: Family) extends IteratingSystem(family) {

  override def update(deltaTime: Float): Unit = {
    getEntities.par.foreach(e =>
      processEntity(e, deltaTime)
    )
  }

}
