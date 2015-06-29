package org.home.components

import org.home.components.model.{UserModel, UserSession}

import scala.concurrent._

trait RepositoryComponent {
  val repository: Repository

  trait Repository {

    def findUserByLoginAndEmail(login: String, password: String): Future[Option[UserModel]]

    def createSession(userSession: UserSession): Future[UserSession]

    def registerUser(userModel: UserModel): Future[Boolean]
  }

}
