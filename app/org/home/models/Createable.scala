package org.home.models

trait Createable[T] {

  def create(): T
}
