package org.home.components.model

import org.home.actors.GenericState

case class PlayerItemState(id: String, name: String, itemType: Int, props: Map[String, String]) extends GenericState {
}
