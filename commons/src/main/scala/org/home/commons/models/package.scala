package org.home

import org.home.commons.models.actions.PlayerAction

import scala.collection.mutable.ArrayBuffer

package object models {
  type ActionQu = ArrayBuffer[PlayerAction]

  def emptyActionQu: ArrayBuffer[PlayerAction] = ArrayBuffer.empty[PlayerAction]
}
