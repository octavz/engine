package org.home.game.player.item

import org.home.messages.MoveInSectorEvent
import org.home.models.ItemState
import org.home.utils.{ActionType, Randomizer, Vector3D}
import com.softwaremill.quicklens._
import org.home.models.actions.PlayerAction

class Ship(shipState: ItemState) extends PlayerItem(shipState) {

  def registerMoveAction(a: MoveInSectorEvent): Unit = {
    val newQu = state.qu.filterNot(_.actionType == ActionType.MOVE_SECTOR)
    newQu += PlayerAction(id = Randomizer.nextId
      , actionType = ActionType.MOVE_SECTOR
      , createdOn = a.currentTime
      , lastModified = a.currentTime
      , target = Some(state.id)
      , data = Some(a.to.toString))
    state.qu.clear()
    state.qu ++= newQu
  }

  override def receive: Receive = {
    case a: MoveInSectorEvent => registerMoveAction(a)
  }

  def removeActionFromQu(id: String): Unit = state.qu ++= state.qu.filterNot(_.id == id)

  def speed: Int = shipState.props.getOrElse("speed", throw new Exception("Ships should have speed")).toInt

  override def updateTurnState(time: Long): ItemState = synchronized {
    state.qu.filter(_.actionType == ActionType.MOVE_SECTOR).foreach(performMove)
    state
  }

  protected def performMove(action: PlayerAction) = {
    //this maybe should return a new action after turn with the left distance
    val serFinalPosition = action.data.getOrElse(throw new RuntimeException("No data for move action"))
    val finalPosition = Vector3D.fromString(serFinalPosition)
    val newPos = Vector3D.getNextPoint(state.location.sectorPosition, finalPosition, 1, speed)
    if (Vector3D.dist(newPos, finalPosition) > speed) {
      state = state.modify(_.location.sectorPosition).setTo(newPos)
    } else {
      removeActionFromQu(action.id)
    }
  }


}
