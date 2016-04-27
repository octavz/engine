package org.home.game.systems


import com.badlogic.ashley.core.{ComponentMapper, Entity, Family}
import com.badlogic.ashley.systems.IteratingSystem
import org.home.game.components.{LocationComponent, QueueComponent, VelocityComponent}
import org.home.models.actions.PlayerAction
import org.home.utils.{ActionType, Vector3D}
import scala.concurrent.Future
import  scala.concurrent.ExecutionContext.Implicits.global

class SectorMovementSystem
  extends IteratingSystem(Family.all(classOf[LocationComponent], classOf[VelocityComponent], classOf[QueueComponent]).get()) {

  private val mapperLocation = ComponentMapper.getFor(classOf[LocationComponent])
  private val mapperVelocity = ComponentMapper.getFor(classOf[VelocityComponent])
  private val mapperQueue = ComponentMapper.getFor(classOf[QueueComponent])

  override def processEntity(entity: Entity, deltaTime: Float): Unit = Future {
    val location = mapperLocation.get(entity)
    val velocity = mapperVelocity.get(entity)
    val qu = mapperQueue.get(entity)
    def performMove(action: PlayerAction) = {
      val speed = velocity.magnitude
      val serFinalPosition = action.data.getOrElse(throw new RuntimeException("No final position for move action"))
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
