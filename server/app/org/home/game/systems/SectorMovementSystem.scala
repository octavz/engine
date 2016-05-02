package org.home.game.systems

import com.badlogic.ashley.core.{ComponentMapper, Entity, Family}
import org.home.game.components.{LocationComponent, QueueComponent, SpeedComponent}
import org.home.models.actions.PlayerAction
import org.home.utils.{ActionType, Vector3D}

class SectorMovementSystem
  extends ParallelIteratingSystem(Family.all(classOf[LocationComponent], classOf[SpeedComponent], classOf[QueueComponent]).get()) {

  val DESTINATION = "destination"

  private val mapperLocation = ComponentMapper.getFor(classOf[LocationComponent])
  private val mapperSpeed = ComponentMapper.getFor(classOf[SpeedComponent])
  private val mapperQueue = ComponentMapper.getFor(classOf[QueueComponent])

  override def processEntity(entity: Entity, deltaTime: Float): Unit = {
    val location = mapperLocation.get(entity)
    val velocity = mapperSpeed.get(entity)
    val qu = mapperQueue.get(entity)
    def performMove(action: PlayerAction) = {
      val speed = velocity.value
      val serFinalPosition = action.data.getOrElse(DESTINATION, throw new RuntimeException("No final position for move action"))
      val finalPosition = Vector3D.fromString(serFinalPosition)
      val newPos = Vector3D.getNextPoint(location.sectorPosition, finalPosition, 1, speed)
      if (Vector3D.dist(newPos, finalPosition) > speed) {
        location.sectorPosition.update(newPos)
      } else {
        qu.content.remove(qu.content.indexWhere(_.id == action.id))
      }
    }
    qu.content.filter(_.actionType == ActionType.MOVE_SECTOR).foreach(performMove)
  }


}
