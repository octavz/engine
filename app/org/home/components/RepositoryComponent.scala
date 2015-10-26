package org.home.components

import org.home.dto.PlayerDTO
import org.home.models._
import org.home.models.universe._

import scala.concurrent._

trait RepositoryComponent {
  val repository: Repository

  trait Repository {

    def saveUniverse(universe: Universe, forceRestart: Boolean): Future[Boolean]

    def loadUniverse(label: String): Future[Option[Universe]]

    def loadAllPlayers(): Future[Seq[PlayerState]]

    def stateForPlayer(userId: String): Future[Option[PlayerState]]

    def findByLoginAndEmail(login: String, password: String): Future[Option[PlayerState]]

    def createSession(userSession: UserSession): Future[UserSession]

    def registerPlayer(playerState: PlayerState): Future[PlayerState]
  }

}
