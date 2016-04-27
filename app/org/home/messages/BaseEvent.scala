package org.home.messages

import org.home.game.components.LocationComponent
import org.home.utils.Vector3D

trait PlayerEvent extends Serializable

trait PlayerItemEvent extends PlayerEvent {
  val itemId: String
}

trait TurnEvent extends Serializable {
  val currentTime: Long
}

trait ServiceEvent extends Serializable

trait EnvEvent extends Serializable


//player generated events
case class MoveInSectorEvent(itemId: String, to: Vector3D, currentTime: Long) extends PlayerItemEvent with TurnEvent

case class PlayerActionEvent(actionType: Int, sessionId: String, actionData: String, currentTime: Long) extends PlayerEvent with TurnEvent

//service related events
case class LoginUserEvent(login: String, password: String) extends ServiceEvent

case class GetPlayerEvent(id: String) extends ServiceEvent

case object SaveUniverseEvent extends ServiceEvent

case object GetUniverseEvent extends ServiceEvent

case object InfoEvent extends ServiceEvent

case class RegisterUserEvent(login: String, password: String, scenario: Int) extends ServiceEvent

case class StateEvent(sessionId: Option[String] = None) extends ServiceEvent

case class NewPlayerItemEvent(itemType: Int, itemProps: Map[String, String], location: LocationComponent) extends ServiceEvent with PlayerEvent

case object ErrorEvent extends ServiceEvent

//environment related events
case object StartEvent extends EnvEvent

case object ShutdownEvent extends EnvEvent

case object GenNewEvent extends EnvEvent

case class TicEvent(currentTime: Long) extends EnvEvent with TurnEvent

case class RandomEvent(eventId: String, eventLifetime: Long) extends EnvEvent

