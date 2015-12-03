package org.home.models.universe

import org.home.utils.{Vector3D, Randomizer}

case class SectorObject(id: String, name: String, objType: Int, position: Vector3D[Long], size: ObjectSize)

object SectorObject extends Createable[SectorObject] {

  override def create(): SectorObject = SectorObject(
    id = Randomizer.nextId,
    name = Randomizer.newRoman(),
    objType = Randomizer.newInt(0, 3),
    position = Vector3D.create[Long](),
    size = ObjectSize.create())
}



