package org.home.commons.game.components

import com.badlogic.ashley.core.Component
import org.home.commons.models.actions.PlayerAction
import org.home.models.ActionQu

import scala.collection.mutable.ArrayBuffer

case class QueueComponent(content: ActionQu = ArrayBuffer.empty[PlayerAction]) extends Component
