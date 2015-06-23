package org.home.components

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.home.components.model._
import org.home.utils.Randomizer

import scala.concurrent.Future
import DbDriver._
import JsonFormats._
import scala.concurrent.ExecutionContext.Implicits.global
import scredis._

trait RepositoryComponentImpl extends RepositoryComponent {
  val repository = new RepositoryImpl
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  class RepositoryImpl extends Repository {
    val redis = new Redis()

    override def findUserByLoginAndEmail(login: String, password: String): Future[Option[UserModel]] = {
      redis.get(login) map {
        res =>
          res.flatMap {
            s =>
              val userModel = mapper.readValue(s, classOf[UserModel])
              if (userModel.password == password) Some(userModel)
              else None
          }
      }
      // Future.successful(Some(UserModel(id = Randomizer.newId, login = login, name = "some name", password = password)))
    }

    override def createSession(userSession: UserSession): Future[Boolean] = {
      redis.set(userSession.sessionId, mapper.writeValueAsString(userSession)) map (_ => true)

      //bucket.set[UserSession](userSession.sessionId, userSession) map (_.isSuccess)
    }

    override def registerUser(userModel: UserModel): Future[Boolean] = {
      redis.set(userModel.id, mapper.writeValueAsString(userModel)) map (_ => true)
      //      bucket.set[UserModel](userModel.id, userModel) map {
      //        _.isSuccess
    }

  }

}
