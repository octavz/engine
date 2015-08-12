package org.home.actors.messages

trait BaseEvent

sealed trait ActionEvent extends BaseEvent with Serializable {
  val eventId: String
  val eventLifetime: Long
}


case class GenericEvent(eventId: String, eventLifetime: Long) extends ActionEvent with Serializable

case object Start extends BaseEvent

case object Shutdown extends BaseEvent

case object GenNew extends BaseEvent

case class LoginUser(login: String, password: String)

case class RegisterUser(login: String, password: String, scenario: Int)

case class NewPlayerItem(itemType: Int, itemProps: Map[String, String])

case class GetPlayerState(userId: String)

case class State(sessionId: Option[String] = None)

case object SaveUniverse

case object GetUniverse

case object Tic

case object Info

case object NoError

case object Error
