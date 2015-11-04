package org.home.models

import javax.annotation.Resource

import org.home.actors.GenericState
import org.home.utils.{ResourceNames, ItemType, Randomizer}

case class ItemState(id: String, name: String, itemType: Int, props: Map[String, String], location: UniverseLocation ) extends GenericState {}

case class ResState(id: String, name: String, itemType: Int, props: Map[String, String] ) extends GenericState {}

object ItemFactory {

  def newBasicShip(location: UniverseLocation) = ItemState(
    id = Randomizer.nextId
    , name = Randomizer.newName
    , itemType = ItemType.SHIP
    , location = location
    , props = Map.empty)

  def newBasicBase(location: UniverseLocation) = ItemState(
    id = Randomizer.nextId
    , name = Randomizer.newName
    , itemType = ItemType.BASE
    , location = location
    , props = Map.empty
  )

  def newResource(resType: Int, value: Long) = ResState(
    id = Randomizer.nextId
    , name = ResourceNames(resType)
    , itemType = resType
    , props = Map("value" -> value.toString)
  )

}

