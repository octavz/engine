package org.home.models


import org.home.models.universe.Universe
import org.home.utils.{ItemType, Randomizer}

import scala.collection.immutable.Queue

case class PlayerState(owner: UserModel
                       , qu: ActionQu
                       , startSector: String
                       , items: List[ItemState])

object PlayerState {
  def newPlayer(owner: UserModel, scenario: Int, universe: Universe) = scenario match {
    case 0 => PlayerState(
      owner = owner
      , qu = emptyActionQu
      , startSector = Randomizer.someSector(universe).id
      , items = List(
        ItemFactory.newBasicShip
        , ItemFactory.newBasicBase
        , ItemFactory.newResource(ItemType.RES_MONEY, 100)
      ))
  }

}

