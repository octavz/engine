package org.home

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.lang.reflect.{Type, ParameterizedType}
import com.fasterxml.jackson.core.`type`.TypeReference

package object utils {

  implicit val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def fromJson[T: Manifest](value: String): T =
    mapper.readValue(value, typeReference[T])

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
