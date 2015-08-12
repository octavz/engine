package org.home.models.universe

import org.home.utils.Randomizer

case class ObjectSize(width: Long, length: Long, height: Long) {

}

object ObjectSize extends Createable[ObjectSize] {
  def rand = Randomizer.newLong(10, 1000)

  override def create(): ObjectSize = ObjectSize(rand, rand, rand)
}
