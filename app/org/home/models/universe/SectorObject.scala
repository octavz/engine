package org.home.models.universe

import org.home.game.components.SizeComponent
import org.home.utils.{Randomizer, Vector3D}

case class SectorObject(id: String, name: String, objType: Int, position: Vector3D, size: SizeComponent)

object SectorObject extends Createable[SectorObject] {
  def randSize = Randomizer.newInt(10, 1000)

  override def create(): SectorObject = SectorObject(
    id = Randomizer.nextId,
    name = Randomizer.newRoman(),
    objType = Randomizer.newInt(0, 3),
    position = Vector3D.create(),
    size = SizeComponent(randSize, randSize, randSize))
}



