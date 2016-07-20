package org.home.commons.utils

import scala.reflect.{ClassTag, _}

case class Vector3D(var x: Long, var y: Long, var z: Long) {

  def update(newX: Long, newY: Long, newZ: Long): Vector3D = {
    x = newX
    y = newY
    z = newZ
    this
  }

  def update(newVector: Vector3D): Vector3D = {
    x = newVector.x
    y = newVector.y
    z = newVector.z
    this
  }

  override def toString: String = s"$x,$y,$z"

}

object Vector3D {
  def create(): Vector3D =
    Vector3D(Randomizer.newNumeric[Long](), Randomizer.newNumeric[Long](), Randomizer.newNumeric[Long]())

  def fromString(str: String): Vector3D =
    str.split(",").toList match {
      case a :: b :: c :: Nil ⇒ Vector3D(a.convert[Long], b.convert[Long], c.convert[Long])
      case _ => throw new Exception("String cannot be read as set of coordinates.")
    }

  @inline
  def direction(v0: Vector3D, v1: Vector3D): Vector3D = Vector3D(v1.x - v0.x, v1.y - v0.y, v1.z - v0.z)

  def getNextPoint(v0: Vector3D, v1: Vector3D, time: Int = 1, speed: Int): Vector3D = {
    val d = dist(v0, v1)
    @inline def component(delta: Long) = speed * time * delta / d
    val x = component(v1.x - v0.x)
    val y = component(v1.y - v0.y)
    val z = component(v1.z - v0.z)
    Vector3D(x.toLong, y.toLong, z.toLong) //keep hi precision for units to have this as accurate as possible
  }

  implicit class ConvertFromString(s: String) {
    def convert[T <: AnyVal : ClassTag]: T = {
      if (classTag[T].runtimeClass == classOf[Int]) s.toInt.asInstanceOf[T]
      else if (classTag[T].runtimeClass == classOf[Long]) s.toLong.asInstanceOf[T]
      else if (classTag[T].runtimeClass == classOf[Double]) s.toDouble.asInstanceOf[T]
      else throw new RuntimeException(s"Cannot convert ${classTag[T].runtimeClass.getName}")
    }
  }

  @inline
  def sqr(v: Long): Double = Math.pow(v.toDouble, 2)

  @inline
  def dist(p1: Vector3D, p2: Vector3D): Double = {
    Math.sqrt(sqr(p1.x - p2.x)
      + sqr(p1.y - p2.y)
      + sqr(p1.z - p2.z))
  }


}
