package org.home.components

import org.home.models._
import org.home.models.universe._

import scala.concurrent._

trait RepositoryComponent {
  val repository: Repository

  trait Repository {
    def saveUniverse(universe: Universe): Future[Boolean]

    def loadUniverse(label: String): Future[Option[Universe]]

    def stateForUser(userId: String): Future[Option[PlayerState]]

    def findUserByLoginAndEmail(login: String, password: String): Future[Option[PlayerState]]

    def createSession(userSession: UserSession): Future[UserSession]

    def registerUser(playerState: PlayerState): Future[PlayerState]
  }

}
