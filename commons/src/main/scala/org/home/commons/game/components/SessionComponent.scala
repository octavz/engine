package org.home.commons.game.components

import com.badlogic.ashley.core.Component

case class SessionComponent(userId: String, sessionId: String) extends Component
