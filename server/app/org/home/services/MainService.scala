package org.home.services

import javax.inject._

import com.badlogic.ashley.core.Entity
import org.home.game.components.{SessionComponent, UserComponent}
import org.home.models.universe.{FullUniverse, Universe}
import org.home.repositories.Repository
import org.home.utils.Randomizer._
import org.home.utils.AshleyScalaModule._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class MainService @Inject()(repository: Repository) {

  def loadUniverse(forceRestart: Boolean = false): Future[FullUniverse] = {

    def createNew = {
      val u = Universe.create()
      repository.saveUniverse(u, forceRestart) map {
        case true => u
        case _ => throw new RuntimeException("Universe cannot be created")
      }
    }

    if (forceRestart) {
      createNew map (u => FullUniverse(u, Seq.empty))
    } else {
      repository.loadUniverse("main") flatMap {
        case Some(u) => repository.loadAllPlayers() flatMap {
          players =>
            repository.loadAllSessions() map {
              sessions =>
                FullUniverse(u, players)
            }
        }
        case _ => throw new Exception("Universe main not found")
      }
    }
  }

  def saveUniverse(universe: Universe): Future[Boolean] =
    repository.saveUniverse(universe, forceRestart = false)

  def loginUser(login: String, password: String): Future[SessionComponent] = {
    for {
      player <- repository
        .findByLoginAndEmail(login, password)
        .map(_.getOrElse(throw new Exception(s"User $login not found")))
      session <- repository.createSession(SessionComponent(player.component[UserComponent].id, nextId))
    } yield {
      session
    }
  }

  def registerUser(model: UserComponent): Future[Boolean] = repository.registerPlayer(model)

  def stateForPlayer(id: String): Future[Option[Entity]] = repository.stateForPlayer(id)

  def findSession(sessionId: String): Future[Option[SessionComponent]] = repository.findSession(sessionId)

  def persistEntity(entity: Entity): Future[Boolean] = {
    repository.updateEntity(entity)
  }

}
