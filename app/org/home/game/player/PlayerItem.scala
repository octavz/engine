package org.home.game.player

import akka.actor.Props
import org.home.game.factory.Factory
import org.home.game.player.item.Ship
import org.home.models.ItemState
import org.home.utils._

object PlayerItem {
  def props(state: ItemState): Props = {
    state.itemType match {
      case ItemType.SHIP => Props(new Ship(state))
      case ItemType.BASE => Props(new Factory(state))
    }
  }
}








