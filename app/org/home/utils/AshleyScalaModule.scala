package org.home.utils

import com.badlogic.ashley.core.{Component, Entity, PooledEngine}

import scala.reflect.{ClassTag, _}
import org.home.game.components._
import org.home.utils._
import play.api.libs.json._
import scala.collection.JavaConversions._

object AshleyScalaModule {

  case class JsonComponent(compType: Int, comp: Component)

  implicit class EntityOps(e: Entity) {

    def component[T <: Component : ClassTag]: T = classTag[T] match {
      case x => e.getComponent[T](x.runtimeClass.asInstanceOf[Class[T]])
    }

    //TODO make valid json
    def asJson()(implicit excludedComponents: Seq[Class[_]] = Seq.empty): String = {
      val lst = e.getComponents
        .filter(x => !excludedComponents.contains(x.getClass))
        .map(x => s"""{"${x.getClass.getSimpleName}":${x.toJson}}""")
      s"[${lst.mkString(",")}]"
    }

  }

  implicit class StringEntityOps(s: String) {
    private val COMPONENT_MAPPER: Seq[Class[_]] = Seq(
      classOf[ChildComponent]
      , classOf[ItemTypeComponent]
      , classOf[LocationComponent]
      , classOf[PlayerComponent]
      , classOf[QueueComponent]
      , classOf[ResourcesComponent]
      , classOf[SizeComponent]
      , classOf[StateComponent]
      , classOf[UserComponent]
      , classOf[VelocityComponent])

    def toEntity(pooledEngine: Option[PooledEngine] = None): Entity = {
      val entity = pooledEngine match {
        case Some(engine) =>
          engine.createEntity()
        case _ => new Entity
      }
      val json = Json.parse(s).as[JsArray]
      val types: Map[String, Class[_]] = COMPONENT_MAPPER.map(_.getSimpleName).zip(COMPONENT_MAPPER).toMap

      json.value.foreach { js =>
        val jsObject = js.as[JsObject]
        val componentType = jsObject.value.keys.head
        val componentData = jsObject.value.values.head.as[JsObject].toString
        val clazz = types.getOrElse(componentType, throw new Exception(s"Type $componentType not found in component type mapper"))
        entity.add(fromJsonWithClass(componentData, clazz).asInstanceOf[Component])
      }
      entity
    }
  }


}
