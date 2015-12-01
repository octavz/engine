package org.home.actors.messages

import org.home.dto.PlayerActionDTO
import org.home.models.UniverseLocation
import org.home.models.universe.SectorPosition
import org.home.models.EnvState

trait BaseEvent

sealed trait ActionEvent extends BaseEvent with Serializable {
  val eventId: String
  val eventLifetime: Long
}

case class GenericEvent(eventId: String, eventLifetime: Long) extends ActionEvent with Serializable

case object StartEvent extends BaseEvent

case object ShutdownEvent extends BaseEvent

case object GenNewEvent extends BaseEvent

case class LoginUserEvent(login: String, password: String)

case class RegisterUserEvent(login: String, password: String, scenario: Int)

case class NewPlayerItemEvent(itemType: Int, itemProps: Map[String, String], location: UniverseLocation)

case class StateEvent(sessionId: Option[String] = None)

case class MoveInSectorAction(itemId: String, to: SectorPosition)

case class PlayerActionEvent[T](actionType: Int, sessionId: Option[String], action: T)

case class GetPlayerEvent(id: String)

case object SaveUniverseEvent

case object GetUniverseEvent

case class TicEvent(time: Long)

case object InfoEvent

case object NoErrorEvent

case object ErrorEvent
