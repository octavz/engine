package org.home.services

import com.badlogic.ashley.core.Entity
import org.home.game.components.{PlayerComponent, UserComponent}
import org.home.models.UserSession
import org.home.models.universe.{FullUniverse, Universe}
import org.home.repositories.RepositoryComponent
import org.home.utils.Randomizer._
import org.home.utils.AshleyScalaModule._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UniverseService {
  this: RepositoryComponent =>

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

  def loginUser(login: String, password: String): Future[Entity] = {
    for {
      player <- repository
        .findByLoginAndEmail(login, password)
        .map(_.getOrElse(throw new Exception("User not found")))
      session <- repository.createSession(UserSession(player.component[UserComponent].data.id, nextId))
    } yield {
      player.add(PlayerComponent(session))
      player
    }
  }

  def registerUser(player: Entity): Future[Entity] =
    repository.registerPlayer(player) flatMap {
      _ =>
        val model = player.component[UserComponent].data
        loginUser(model.login, model.password)
    }

  def stateForPlayer(id: String): Future[Option[Entity]] = repository.stateForPlayer(id)

  def findSession(sessionId: String): Future[Option[UserSession]] = repository.findSession(sessionId)

}
