package org.home.models.universe

import org.home.utils.Randomizer

case class SectorPosition(x: Long, y: Long, z: Long) {
}

object SectorPosition extends Createable[SectorPosition] {
  override def create(): SectorPosition = SectorPosition(Randomizer.newLong(), Randomizer.newLong(), Randomizer.newLong())
}

