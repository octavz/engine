package org.home.components.model.universe

import org.home.utils.Randomizer

case class Sector(id: String, name: String, objects: List[SectorObject]) {
  override def toString = {
    s"[$id - $name]"
  }

}

object Sector extends Createable[Sector] {

  override def create(): Sector = {
    Sector(
      id = Randomizer.nextId,
      name = Randomizer.newName,
      objects = List.range(1, Randomizer.newInt(1, 5)).map(_ => SectorObject.create()))
  }

}

