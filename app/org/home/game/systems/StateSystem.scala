package org.home.game.systems

import javax.inject.Inject

import com.badlogic.ashley.core.{Entity, Family}
import com.badlogic.ashley.systems.IteratingSystem
import org.home.game.components.StateComponent
import org.home.services.UniverseService

class StateSystem (service: UniverseService) extends IteratingSystem(Family.all(classOf[StateComponent]).get()) {


  override def processEntity(entity: Entity, deltaTime: Float): Unit = service.persistEntity(entity)


}
