package org.home

import scala.collection.immutable.Queue

package object models {
  type ActionQu = Queue[PlayerAction]

  def emptyActionQu = Queue.empty[PlayerAction]
}
