package org.home.models

import org.home.models.universe.{SectorPosition, Universe}
import org.home.utils.{ItemType, Randomizer, ScenarioType}

case class PlayerState(owner: UserModel
                       , qu: ActionQu
                       , startSector: String
                       , items: List[ItemState])

object PlayerState {
  def newPlayer(owner: UserModel, scenario: Int, universe: Universe) = {
    val startSector = Randomizer.someSector(universe).id
    val (x, y, z) = (0, 0, 0)

    scenario match {
      case ScenarioType.NORMAL => PlayerState(
        owner = owner
        , qu = emptyActionQu
        , startSector = startSector
        , items = List(
          ItemFactory.newBasicShip(UniverseLocation(startSector, SectorPosition(x, y, z)))
          , ItemFactory.newResource(ItemType.RES_MONEY, 100)
        ))
    }
  }
}

