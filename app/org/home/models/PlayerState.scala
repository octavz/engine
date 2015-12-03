package org.home.models

import org.home.models.universe.{ Universe}
import org.home.utils.{Vector3D, ItemType, Randomizer, ScenarioType}

import scala.collection.mutable.ArrayBuffer

case class PlayerState(owner: UserModel
                       , qu: ActionQu
                       , startSector: String
                       , items: ArrayBuffer[ItemState]
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
        , items = ArrayBuffer(
          ItemFactory.newBasicShip(UniverseLocation(startSector, Vector3D(x, y, z))))
        , resources = List(ItemFactory.newResource(ItemType.RES_MONEY, DEFAULT_MONEY))
      )
    }
  }
}

