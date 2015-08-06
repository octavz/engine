package org.home.components.model.universe

trait Createable[T] {

  def create(): T
}
