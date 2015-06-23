package org.home.components

import org.home.components.model.{UserSession, UserModel}

import scala.concurrent._
import org.home.utils.Randomizer

trait RepositoryComponent {
  val repository: Repository

  trait Repository {

    def findUserByLoginAndEmail(login: String, password: String): Future[Option[UserModel]]

    def createSession(userSession: UserSession): Future[UserSession]

    def registerUser(userModel: UserModel): Future[Boolean]
  }
}

