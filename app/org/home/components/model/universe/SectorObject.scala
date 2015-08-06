package org.home.components.model.universe

import org.home.utils.Randomizer

case class SectorObject(id: String, name: String, objType: Int, position: SectorPosition, size: ObjectSize)

object SectorObject extends Createable[SectorObject] {

  override def create(): SectorObject = SectorObject(
    id = Randomizer.nextId,
    name = Randomizer.newRoman(),
    objType = Randomizer.newInt(0, 3),
    position = SectorPosition.create(),
    size = ObjectSize.create())
}



