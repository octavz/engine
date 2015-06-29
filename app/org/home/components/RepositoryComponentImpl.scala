package org.home.components

import org.home.components.model.JsonFormats._
import org.home.components.model._
import play.api.libs.json.Json
import scredis._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RepositoryComponentImpl extends RepositoryComponent {
  override val repository: Repository = new RepositoryImpl

  class RepositoryImpl extends Repository {
    val redis = new Redis()

    override def findUserByLoginAndEmail(login: String, password: String): Future[Option[UserModel]] = {
      redis.get(login) map {
        res =>
          res.flatMap {
            s =>
              val userModel = Json.parse(s).as[UserModel]
              if (userModel.password == password) Some(userModel)
              else None
          }
      }
      // Future.successful(Some(UserModel(id = Randomizer.newId, login = login, name = "some name", password = password)))
    }

    override def createSession(userSession: UserSession): Future[UserSession] = {
      redis.set(userSession.sessionId, Json.toJson(userSession).toString()) map (_ => userSession)
      //bucket.set[UserSession](userSession.sessionId, userSession) map (_.isSuccess)
    }

    override def registerUser(userModel: UserModel): Future[Boolean] = {
      redis.set(userModel.login, Json.toJson(userModel).toString()) map (_ => true)
      //      bucket.set[UserModel](userModel.id, userModel) map {
      //        _.isSuccess
    }


  }

}
