package org.home.models.actions

case class PlayerAction(id: String, actionType: Int, createdOn: Long, lastModified: Long, target: Option[String], data: Option[String])

