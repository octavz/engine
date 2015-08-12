package org.home.models.universe

trait Createable[T] {

  def create(): T
}
