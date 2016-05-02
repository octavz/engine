package org.home.messages


trait EnvEvent extends Serializable


case object GenNewEvent extends EnvEvent

case class TicEvent(currentTime: Long) extends EnvEvent

case class RandomEvent(eventId: String, eventLifetime: Long) extends EnvEvent

