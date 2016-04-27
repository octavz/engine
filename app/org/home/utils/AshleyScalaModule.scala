package org.home.utils

import com.badlogic.ashley.core.{Component, Entity}

import scala.reflect.{ClassTag, _}

object AshleyScalaModule {

  implicit class EntityOps(e: Entity) {

    def component[T <: Component : ClassTag ]: T = classTag[T] match {
      case x => e.getComponent[T](x.runtimeClass.asInstanceOf[Class[T]])
    }

  }


}
