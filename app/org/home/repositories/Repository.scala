package org.home.repositories

import com.badlogic.ashley.core.Entity
import org.home.game.components.{SessionComponent, UserComponent}
import org.home.models._
import org.home.models.universe._

import scala.concurrent._

trait Repository {

  def updateEntity(entity: Entity): Future[Boolean]

  def saveUniverse(universe: Universe, forceRestart: Boolean): Future[Boolean]

  def loadUniverse(label: String): Future[Option[Universe]]

  def loadAllPlayers(): Future[Seq[Entity]]

  def loadAllSessions(): Future[Seq[SessionComponent]]

  def stateForPlayer(userId: String): Future[Option[Entity]]

  def findByLoginAndEmail(login: String, password: String): Future[Option[Entity]]

  def createSession(userSession: SessionComponent): Future[SessionComponent]

  def registerPlayer(player: UserComponent): Future[Boolean]

  def findSession(sessionId: String): Future[Option[SessionComponent]]
}

