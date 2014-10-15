package org.home.components

import org.home.components.model._
import org.home.utils.Randomizer

import scala.concurrent.Future
import DbDriver._
import JsonFormats._
import scala.concurrent.ExecutionContext.Implicits.global

trait RepositoryComponentImpl extends RepositoryComponent {
  val repository = new RepositoryImpl

  class RepositoryImpl extends Repository {
    override def findUserByLoginAndEmail(login: String, password: String): Future[Option[UserModel]] =
      Future.successful(Some(UserModel(id = Randomizer.newId, login = login, name = "some name", password = password)))

    override def createSession(userSession: UserSession): Future[Boolean] = bucket.set[UserSession](userSession.sessionId, userSession) map (_.isSuccess)

    override def registerUser(userModel: UserModel): Future[Boolean] = {
      bucket.set[UserModel](userModel.id, userModel) map { _.isSuccess }

    }
  }

}
