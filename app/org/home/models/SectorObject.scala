package org.home.models

import org.home.utils.Randomizer

case class SectorObject(id: String, name: String, objType: Int, position: SectorPosition, size: ObjectSize)

object SectorObject extends Createable[SectorObject] {

  override def create(): SectorObject = SectorObject(
    id = Randomizer.newId,
    name = Randomizer.newString(),
    objType = Randomizer.newInt(0, 3),
    position = SectorPosition.create(),
    size = ObjectSize.create())
}



