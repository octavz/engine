package org.home.models

import javax.annotation.Resource

import org.home.actors.GenericState
import org.home.utils.{ResourceNames, ItemType, Randomizer}

case class ItemState(id: String, name: String, itemType: Int, props: Map[String, String], location: Option[UniverseLocation] = None) extends GenericState {}

object ItemFactory {

  def newBasicShip(location: UniverseLocation) = ItemState(
    id = Randomizer.nextId
    , name = Randomizer.newName
    , itemType = ItemType.SHIP
    , location = Some(location)
    , props = Map.empty)

  def newBasicBase = ItemState(
    id = Randomizer.nextId
    , name = Randomizer.newName
    , itemType = ItemType.BASE
    , props = Map.empty
  )

  def newResource(resType: Int, value: Long) = ItemState(
    id = Randomizer.nextId
    , name = ResourceNames(resType)
    , itemType = resType
    , props = Map("value" -> value.toString)
  )

}

