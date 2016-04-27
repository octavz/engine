package org.home.game.components

import com.badlogic.ashley.core.Component
import org.home.models.UserSession

case class PlayerComponent(sessionId: UserSession) extends  Component
