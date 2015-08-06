package org.home.components

import org.home.components.model._
import org.home.components.model.universe._

import scala.concurrent._

trait RepositoryComponent {
  val repository: Repository

  trait Repository {
    def getPlayerState(userId: String): Future[Option[PlayerState]]

    def findUserByLoginAndEmail(login: String, password: String): Future[Option[UserModel]]

    def createSession(userSession: UserSession): Future[UserSession]

    def registerUser(userModel: UserModel): Future[UserModel]
  }

}
