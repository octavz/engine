package org.home.commons.models.universe

trait Createable[T] {

  def create(): T
}
