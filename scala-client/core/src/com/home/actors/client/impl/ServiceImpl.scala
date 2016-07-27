package com.home.actors.client.impl

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.home.actors.client.Service
import org.home.commons.dto.LoginDTO

class ServiceImpl(var engine: PooledEngine) extends Service {
  final private val json: Json = new Json(JsonWriter.OutputType.json)

  def getEngine: PooledEngine = engine


  private def jsonRequest[T](dto: T): Net.HttpRequest = {
    val request: Net.HttpRequest = new Net.HttpRequest("POST")
    request.setUrl("http://localhost:9000/login")
    request.setHeader("Content-Type", "application/json")
    request.setHeader("Accept", "application/json")
    val content: String = json.toJson(dto)
    request.setContent(content)
    request
  }

  def login(login: String, pass: String) {
    val dto  =  LoginDTO(login, pass)
    val request: Net.HttpRequest = jsonRequest(dto)
    Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
      def handleHttpResponse(httpResponse: Net.HttpResponse) {
        val root: JsonValue = new JsonReader().parse(httpResponse.getResultAsString)
//        engine.addEntity(parse(root.getChild("player").parent))
        import scala.collection.JavaConversions._
//        for (item <- root.getChild("items").parent) {
//          engine.addEntity(parse(item))
//        }
      }

      def failed(t: Throwable) {
        throw new RuntimeException(t)
      }

      def cancelled() {
        //don't call this here
      }
    })
  }
}
