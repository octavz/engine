package org.home.game.factory

import org.home.game.player.item.PlayerItem
import org.home.models.ItemState

class Factory(factoryState: ItemState) extends PlayerItem(factoryState) {

  def updateTurnState(time: Long): ItemState = ???
}
