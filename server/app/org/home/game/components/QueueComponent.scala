package org.home.game.components

import com.badlogic.ashley.core.Component
import org.home.models.ActionQu
import org.home.models.actions.PlayerAction

import scala.collection.mutable.ArrayBuffer

case class QueueComponent(content: ActionQu = ArrayBuffer.empty[PlayerAction]) extends Component
