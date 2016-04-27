package org.home.repositories

import com.badlogic.ashley.core.Entity
import org.home.models._
import org.home.models.universe._

import scala.concurrent._

trait RepositoryComponent {
  val repository: Repository

  trait Repository {

    def saveUniverse(universe: Universe, forceRestart: Boolean): Future[Boolean]

    def loadUniverse(label: String): Future[Option[Universe]]

    def loadAllPlayers(): Future[Seq[Entity]]

    def loadAllSessions(): Future[Seq[UserSession]]

    def stateForPlayer(userId: String): Future[Option[Entity]]

    def findByLoginAndEmail(login: String, password: String): Future[Option[Entity]]

    def createSession(userSession: UserSession): Future[UserSession]

    def registerPlayer(player: Entity): Future[Entity]

    def findSession(sessionId: String): Future[Option[UserSession]]
  }

}
