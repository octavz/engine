package org.home.models

import org.home.game.components.LocationComponent
import org.home.game.player.item.ItemFactory
import org.home.models.universe.Universe
import org.home.utils.{ItemType, Randomizer, ScenarioType, Vector3D}

import scala.collection.mutable.ArrayBuffer

case class PlayerState(owner: UserModel
                       , qu: ActionQu
                       , startSector: String
                       , items: ArrayBuffer[ItemState]
                       , resources: List[ResState])

object PlayerState {
  val DEFAULT_MONEY = 100L

  def newPlayer(owner: UserModel, scenario: Int, universe: Universe): PlayerState = {
    val startSector = Randomizer.someSector(universe).id
    val (x, y, z) = (0L, 0L, 0L)

    scenario match {
      case ScenarioType.NORMAL => PlayerState(
        owner = owner
        , qu = emptyActionQu
        , startSector = startSector
        , items = ArrayBuffer(
          ItemFactory.newBasicShip(LocationComponent(startSector, Vector3D(x, y, z))))
        , resources = List(ItemFactory.newResource(ItemType.RES_MONEY, DEFAULT_MONEY))
      )
    }
  }
}

