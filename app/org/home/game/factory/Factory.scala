package org.home.game.factory

import org.home.game.player.item.PlayerItem
import org.home.models.ItemState

/**
  * Created by octav on 21.04.2016.
  */
class Factory(factoryState: ItemState) extends PlayerItem(factoryState) {
  override def updateTurnState(): Unit = ???
}
