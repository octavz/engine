package org.home.actors.org.home.actors.mesages

trait BaseEvent extends Serializable

sealed trait ActionEvent extends BaseEvent with Serializable{
  val eventId: String
  val eventLifetime: Long
}


case class GenericEvent(eventId: String, eventLifetime: Long) extends ActionEvent with Serializable

case object Start extends BaseEvent with Serializable
case object Shutdown extends BaseEvent with Serializable
case object GenNew extends BaseEvent with Serializable
