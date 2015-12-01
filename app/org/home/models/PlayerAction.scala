package org.home.models

case class PlayerAction(actionType: Int, createdOn: Long, finishOn: Long, target: String, data: Option[String])

