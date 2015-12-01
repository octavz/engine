package org.home.models.universe

import org.home.utils.Randomizer

case class SectorPosition(x: Long, y: Long, z: Long) {
  override def toString(): String = s"$x,$y,$z"
  def fromString(str: String): SectorPosition = str.split(",").toList match {
    case a :: b :: c :: Nil ⇒ SectorPosition(a.toLong, b.toLong, c.toLong)
    case _ => throw new Exception("String cannot be read as set of coordinates.")
  }
}

object SectorPosition extends Createable[SectorPosition] {
  override def create(): SectorPosition = 
    SectorPosition(Randomizer.newLong(), Randomizer.newLong(), Randomizer.newLong())
}

