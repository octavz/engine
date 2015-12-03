package org.home.utils

import org.home.utils.Ops.ConvertFromString

import scala.reflect.ClassTag
import scala.reflect._

case class Vector3D[T <: AnyVal : ClassTag](x: T, y: T, z: T) {
  override def toString: String = s"$x,$y,$z"
}

object Vector3D {
  def create[T <: AnyVal : ClassTag](): Vector3D[T] =
    Vector3D(Randomizer.newNumeric[T](), Randomizer.newNumeric[T](), Randomizer.newNumeric[T]())

  def fromString[T <: AnyVal : ClassTag](str: String): Vector3D[T] =
    str.split(",").toList match {
      case a :: b :: c :: Nil ⇒ Vector3D(a.convert, b.convert, c.convert)
      case _ => throw new Exception("String cannot be read as set of coordinates.")
    }
}

object Ops {

  def getNextPoint(v0: Vector3D[Long], v1: Vector3D[Long], t: Int = 1): Vector3D[Long] = {
    val dir = Vector3D(v1.x - v0.x, v1.y - v0.y, v1.z - v0.z)
    def x(t: Long): Long = v0.x - t * dir.x
    def y(t: Long): Long = v0.y - t * dir.y
    def z(t: Long): Long = v0.z - t * dir.z
    Vector3D(x(t), y(t), z(t))
  }

  implicit class ConvertFromString[T <: AnyVal : ClassTag](s: String) {
    def convert: T = {
      if (classTag[T].runtimeClass == classOf[Int]) s.toInt.asInstanceOf[T]
      else if (classTag[T].runtimeClass == classOf[Long]) s.toLong.asInstanceOf[T]
      else if (classTag[T].runtimeClass == classOf[Double]) s.toDouble.asInstanceOf[T]
      else throw new RuntimeException(s"Cannot convert ${classTag[T].runtimeClass.getName}")
    }
  }

  def sqr(v: Long): Double = Math.pow(v.toDouble, 2)

  def dist(p1: Vector3D[Long], p2: Vector3D[Long]): Double = {
    Math.sqrt(sqr(p1.x - p2.x)
      + sqr(p1.y - p2.y)
      + sqr(p1.z - p2.z))
  }

}
