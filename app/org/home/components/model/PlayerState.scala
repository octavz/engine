package org.home.components.model


import org.home.actors.GenericState

import scala.collection.immutable.Queue

case class PlayerState(owner: UserModel, qu: Queue[Int], startSector: String, itemsState: List[PlayerItemState]) extends GenericState {}

object PlayerState {
  def empty(owner: UserModel) = PlayerState(
    owner = owner, qu = Queue.empty[Int], startSector = "", itemsState = List.empty)
}

