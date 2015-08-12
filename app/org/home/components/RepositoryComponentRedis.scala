package org.home.components

import org.home.models.JsonFormats._
import org.home.models._
import org.home.models.universe.Universe
import play.api.libs.json.Json
import scredis._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.home.utils.Constants._

trait RepositoryComponentRedis extends RepositoryComponent {
  override val repository: Repository = new RepositoryRedis

  class RepositoryRedis extends Repository {
    val redis = new Redis()

    override def findUserByLoginAndEmail(login: String, password: String): Future[Option[PlayerState]] =
      redis.hGet[String](KEY_LOGINS, login) flatMap {
        case Some(id) => redis.get(id) map {
          case Some(s) =>
            val ps = Json.parse(s).as[PlayerState]
            if (ps.owner.password == password) Some(ps) else None
          case _ => None
        }
        case _ => Future.successful(None)
      }


    override def createSession(userSession: UserSession): Future[UserSession] = {
      redis.set(userSession.sessionId, Json.toJson(userSession).toString()) map (_ => userSession)
      //bucket.set[UserSession](userSession.sessionId, userSession) map (_.isSuccess)
    }

    override def registerUser(playerState: PlayerState): Future[PlayerState] =
      redis.hExists(KEY_LOGINS, playerState.owner.login) flatMap {
        exists =>
          if (exists)
            throw new RuntimeException(s"User with login ${playerState.owner.login} already exists!")
          redis.withTransaction {
            t =>
              t.hSet(KEY_LOGINS, playerState.owner.login, playerState.owner.id)
              t.set(playerState.owner.id, Json.toJson(playerState).toString())
          } map (_ => playerState)
      }

    override def stateForUser(userId: String): Future[Option[PlayerState]] = {
      redis.get(userId) map {
        opt => opt.map(json => Json.parse(json).as[PlayerState])
      }
    }

    def universeKey(label: String): String = s"$KEY_UNIVERSE}:$label"

    override def saveUniverse(universe: Universe): Future[Boolean] = {
      val json = Universe.toJson(universe.sectors)
      redis.set(universeKey(universe.label), json)
    }

    override def loadUniverse(label: String): Future[Option[Universe]] =
      redis.get(universeKey(label)) map {
        opt =>
          opt.map(js => Universe(sectors = Universe.fromJson(js), label = label))
      }
  }

}
