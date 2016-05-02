package org.home

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.lang.reflect.{ParameterizedType, Type}

import com.fasterxml.jackson.core.`type`.TypeReference

import scala.reflect.ClassTag

package object utils {

  implicit val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def fromJson[T: Manifest](value: String): T =
    mapper.readValue(value, typeReference[T])

  def fromJsonWithClass[T](value: String, clazz: Class[T]): T =
    mapper.readValue(value, clazz)

  private[this] def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private[this] def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) {
      m.runtimeClass
    }
    else new ParameterizedType {
      def getRawType = m.runtimeClass

      def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

      def getOwnerType = null
    }
  }

  implicit class CustomSerializer(obj: Any) {
    def toJson = {
      val json = mapper.writeValueAsString(obj)
      json
    }
  }


}
