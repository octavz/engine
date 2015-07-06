package org.home.models

import org.home.utils.{SectorObjectEnum, Randomizer}
import org.home.utils.SectorObjectEnum._

case class SectorObject(id: String, name: String, objType: ObjectType, position: SectorPosition, size: ObjectSize)

object SectorObject extends Createable[SectorObject] {

  override def create(): SectorObject = SectorObject(
    id = Randomizer.newId,
    name = Randomizer.newString(),
    objType = SectorObjectEnum.FACTORY, // Randomizer.newFromEnum(SectorObjectEnum),
    position = SectorPosition.create(),
    size = ObjectSize.create())
}



