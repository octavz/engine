package org.home.models

import org.home.models.universe.{SectorPosition, Universe}
import org.home.utils.{ItemType, Randomizer, ScenarioType}

case class PlayerState(owner: UserModel
                       , qu: ActionQu
                       , startSector: String
                       , items: List[ItemState]
                       , resources: List[ResState])

object PlayerState {
  val DEFAULT_MONEY = 100

  def newPlayer(owner: UserModel, scenario: Int, universe: Universe): PlayerState = {
    val startSector = Randomizer.someSector(universe).id
    val (x, y, z) = (0, 0, 0)

    scenario match {
      case ScenarioType.NORMAL => PlayerState(
        owner = owner
        , qu = emptyActionQu
        , startSector = startSector
        , items = List(
          ItemFactory.newBasicShip(UniverseLocation(startSector, SectorPosition(x, y, z))))
        , resources = List(ItemFactory.newResource(ItemType.RES_MONEY, DEFAULT_MONEY))
      )
    }
  }
}

