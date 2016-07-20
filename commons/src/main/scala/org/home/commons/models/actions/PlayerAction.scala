package org.home.commons.models.actions

case class PlayerAction(id: String, actionType: Int, createdOn: Long, lastModified: Long, data: Map[String, String])

