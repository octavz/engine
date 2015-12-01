package org.home

import scala.collection.mutable.ArrayBuffer

package object models {
  type ActionQu = ArrayBuffer[PlayerAction]

  def emptyActionQu: ArrayBuffer[PlayerAction] = ArrayBuffer.empty[PlayerAction]
}
