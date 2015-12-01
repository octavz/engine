package org.home.models



case class PlayerAction(actionType: Int, createdOn: Long, finishOn: Long, target: Option[String], data: Option[String])

