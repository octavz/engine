package org.home.game.player.item

import org.home.utils.{ItemType, Randomizer, ResourceNames}
import org.home.models._
import org.home.models.universe.UniverseLocation

/**
  * Created by octav on 21.04.2016.
  */
object ItemFactory {

  def newBasicShip(location: UniverseLocation): ItemState = ItemState(
    id = Randomizer.nextId
    , name = Randomizer.newName
    , itemType = ItemType.SHIP
    , location = location
    , props = Map("speed" -> "100")
    , qu = emptyActionQu
  )

  def newBasicBase(location: UniverseLocation): ItemState = ItemState(
    id = Randomizer.nextId
    , name = Randomizer.newName
    , itemType = ItemType.BASE
    , location = location
    , props = Map.empty
    , qu = emptyActionQu
  )

  def newResource(resType: Int, value: Long): ResState = ResState(
    id = Randomizer.nextId
    , name = ResourceNames(resType)
    , itemType = resType
    , props = Map("value" -> value.toString
    )
  )

}
