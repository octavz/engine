package org.home.models

import org.home.utils.Randomizer

case class Sector(id: String, name: String, objects: List[SectorObject], neighbours: List[String])

object Sector extends Createable[Sector] {

  override def create(): Sector = {
    Sector(
      id = Randomizer.newId,
      name = Randomizer.newString(),
      objects = List.range(1, 4).map(_ => SectorObject.create()),
      neighbours = List.empty)
  }
}

