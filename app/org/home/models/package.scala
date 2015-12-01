package org.home

import scala.collection.mutable.ArrayBuffer

package object models {
  type ActionQu = ArrayBuffer[PlayerAction]

  def emptyActionQu: ActionQu = ArrayBuffer.empty[PlayerAction]
}
