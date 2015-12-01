package org.home.models

import javax.annotation.Resource

import org.home.actors.GenericState
import org.home.utils.{ResourceNames, ItemType, Randomizer}

import scala.collection.mutable.ArrayBuffer

case class ItemState(id: String
                     , name: String
                     , itemType: Int
                     , props: Map[String, String]
                     , location: UniverseLocation
                     , qu: ActionQu) extends GenericState {
  val speed = props.getOrElse("speed", throw new Exception("Speed is not defined")).toInt
}

case class ResState(id: String
                    , name: String
                    , itemType: Int
                    , props: Map[String, String]) extends GenericState {}

object ItemFactory {

  def newBasicShip(location: UniverseLocation): ItemState = ItemState(
    id = Randomizer.nextId
    , name = Randomizer.newName
    , itemType = ItemType.SHIP
    , location = location
    , props = Map.empty
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

