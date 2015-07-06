package org.home.models

import org.home.utils.Randomizer

case class SectorNeighbour(sector: Sector, distance: Long, label: String)

object SectorNeighbour extends Createable[SectorNeighbour] {
  override def create(): SectorNeighbour = SectorNeighbour(
    sector = Sector.create(),
    distance = Randomizer.newLong(),
    label = Randomizer.newString())
}

