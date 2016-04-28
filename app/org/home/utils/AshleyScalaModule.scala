package org.home.utils

import com.badlogic.ashley.core.{Component, Entity, PooledEngine}

import scala.reflect.{ClassTag, _}
import org.home.game.components._
import org.home.utils._

object AshleyScalaModule {

  case class JsonComponent(compType: Int, comp: Component)


  implicit class EntityOps(e: Entity) {

    def component[T <: Component : ClassTag]: T = classTag[T] match {
      case x => e.getComponent[T](x.runtimeClass.asInstanceOf[Class[T]])
    }

    //TODO make valid json
    def asJson: String = {
      val lst = e.getComponents.toArray().map {
        case x: ChildComponent => s"1:${x.toJson}"
        case x: ItemTypeComponent => s"2:${x.toJson}"
        case x: LocationComponent => s"3:${x.toJson}"
        case x: PlayerComponent => s"4:${x.toJson}"
        case x: QueueComponent => s"5:${x.toJson}"
        case x: ResourcesComponent => s"6:${x.toJson}"
        case x: SizeComponent => s"7:${x.toJson}"
        case x: StateComponent => s"8:${x.toJson}"
        case x: UserComponent => s"9:${x.toJson}"
        case x: VelocityComponent => s"10:${x.toJson}"
      }
      s"${lst.mkString("__")}"
    }

  }

  implicit class StringEntityOps(s: String) {

    def toEntity(pooledEngine: Option[PooledEngine] = None): Entity = {
      val entity = pooledEngine match {
        case Some(engine) =>
          engine.createEntity()
        case _ => new Entity
      }
      s.split("__").foreach { str =>
        val sep = str.indexOf(":")
        val tp = str.substring(0, sep)
        val data = str.substring(sep + 1)
        tp match {
          case "1" => entity.add(fromJson[ChildComponent](data))
          case "2" => entity.add(fromJson[ItemTypeComponent](data))
          case "3" => entity.add(fromJson[LocationComponent](data))
          case "4" => entity.add(fromJson[PlayerComponent](data))
          case "5" => entity.add(fromJson[QueueComponent](data))
          case "6" => entity.add(fromJson[ResourcesComponent](data))
          case "7" => entity.add(fromJson[SizeComponent](data))
          case "8" => entity.add(fromJson[StateComponent](data))
          case "9" => entity.add(fromJson[UserComponent](data))
          case "10" => entity.add(fromJson[VelocityComponent](data))
        }
      }
      entity
    }
  }


}
