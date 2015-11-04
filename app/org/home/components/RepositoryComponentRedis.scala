package org.home.components

import org.home.dto.PlayerDTO
import org.home.models.JsonFormats._
import org.home.models._
import org.home.models.universe.Universe
import play.api.libs.json.Json
import scredis._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.home.utils.Constants._
import scala.collection.immutable.Queue
import com.softwaremill.quicklens._

trait RepositoryComponentRedis extends RepositoryComponent {
  override val repository: Repository = new RepositoryRedis

  implicit class StringOps(s: String) {
    def withNS(ns: String) = s"$ns:$s"

    def woNS(ns: String) = if (s.startsWith(ns)) s.replace(ns, "") else s

    def withUserNS = withNS(USER_NS)

    def woUserNS = woNS(USER_NS)

    def withUniNS: String = withNS(KEY_UNIVERSE)

    def woSessionNS = woNS(SESSION_NS)

    def withSessionNS: String = withNS(SESSION_NS)

    def toPlayerState: PlayerState = Json.parse(s).as[PlayerState].prepare

    def toUserSession: UserSession = Json.parse(s).as[UserSession].modify(_.sessionId).using(_.woNS(SESSION_NS))
  }

  implicit class PlayerOps(ps: PlayerState) {
    def prepare = ps.copy(owner = ps.owner.copy(id = ps.owner.id.woUserNS))
  }

  class RepositoryRedis extends Repository {
    val redis = new Redis()

    override def findByLoginAndEmail(login: String, password: String): Future[Option[PlayerState]] =
      redis.hGet[String](KEY_LOGINS, login) flatMap {
        case Some(id) => redis.get(id.withUserNS) map {
          case Some(s) =>
            val ps = s.toPlayerState
            if (ps.owner.password == password) Some(ps) else None
          case _ => None
        }
        case _ => Future.successful(None)
      }

    override def createSession(userSession: UserSession): Future[UserSession] = {
      redis.set(userSession.sessionId.withSessionNS, Json.toJson(userSession).toString()) map (_ => userSession)
      //bucket.set[UserSession](userSession.sessionId, userSession) map (_.isSuccess)
    }

    override def registerPlayer(playerState: PlayerState): Future[PlayerState] =
      redis.hExists(KEY_LOGINS, playerState.owner.login) flatMap {
        exists =>
          if (exists)
            throw new RuntimeException(s"User with login ${playerState.owner.login} already exists!")
          redis.withTransaction {
            t =>
              t.hSet(KEY_LOGINS, playerState.owner.login, playerState.owner.id)
              t.set(playerState.owner.id.withUserNS, Json.toJson(playerState).toString())
          } map (_ => playerState)
      }

    override def stateForPlayer(userId: String): Future[Option[PlayerState]] =
      redis.get(userId.withUserNS) map {
        opt => opt.map(json => json.toPlayerState)
      }

    override def saveUniverse(universe: Universe, forceRestart: Boolean): Future[Boolean] = {
      val json = Universe.toJson(universe.sectors)
      if (forceRestart) {
        redis.flushDB() flatMap {
          _ =>
            redis.set(universe.label.withUniNS, json) flatMap {
              _ =>
                registerPlayer(PlayerState(
                  owner = UserModel("admin-id", "admin", "Administrator", "a")
                  , qu = Queue.empty
                  , startSector = ""
                  , items = List.empty
                  , resources = List.empty)) map { _ =>
                  true
                }
            }
        }
      } else {
        redis.set(universe.label.withUniNS, json) map (_ => true)
      }
    }

    override def loadUniverse(label: String): Future[Option[Universe]] =
      redis.get(label.withUniNS) map {
        opt =>
          opt.map(js => Universe(sectors = Universe.fromJson(js), label = label))
      }

    override def loadAllPlayers(): Future[Seq[PlayerState]] =
      redis.hGetAll(KEY_LOGINS) flatMap {
        case Some(all) =>
          Future.sequence(all.map {
            case (_, id) => redis.get(id.withUserNS) map {
              case Some(u) => u.toPlayerState
              case None => throw new Exception(s"User $id found in index but not in db")
            }
          }.toSeq)
        case None => throw new Exception("Cannot find index key")
      }

    private def loadSession(k: String): Future[UserSession] = redis.get(k) map (_.get.toUserSession)

    override def loadAllSessions(): Future[Seq[UserSession]] = {
      redis.keys(s"$SESSION_NS:*") flatMap {
        keys =>
          Future.sequence(keys.toSeq.map(loadSession))
      }
    }
  }

}
