package org.home.repositories

import javax.inject.Inject

import akka.actor.ActorSystem
import com.badlogic.ashley.core.Entity
import org.home.models._
import org.home.models.universe.Universe
import scredis._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.home.utils.Constants._
import com.softwaremill.quicklens._
import org.home.game.components.{StateComponent, UserComponent}
import play.api.Logger
import org.home.utils.AshleyScalaModule._
import org.home.utils._

class RepositoryRedis @Inject()(actorSystem: ActorSystem) extends Repository {

  private[this] implicit class StringOps(s: String) {
    def withNS(ns: String): String = s"$ns:$s"

    def woNS(ns: String): String = if (s.startsWith(ns)) s.replace(ns, "") else s

    //    def withUserNS: String = withNS(USER_NS)
    //
    //    def woUserNS: String = woNS(USER_NS)

    def withUniNS: String = withNS(KEY_UNIVERSE)

    def woSessionNS: String = woNS(SESSION_NS)

    def withSessionNS: String = withNS(SESSION_NS)

    def toUserSession: UserSession = fromJson[UserSession](s).modify(_.sessionId).using(_.woNS(SESSION_NS))
  }

  val redis = Redis.withActorSystem()(actorSystem)

  override def findByLoginAndEmail(login: String, password: String): Future[Option[Entity]] =
    redis.hGet[String](KEY_LOGINS, login) flatMap {
      case Some(id) => redis.get(id) map {
        case Some(s) =>
          val ent = s.toEntity()
          val model = ent.component[UserComponent].data
          if (model.password == password) Some(ent) else None
        case _ => None
      }
      case _ => Future.successful(None)
    }

  override def createSession(userSession: UserSession): Future[UserSession] = {
    redis.set(userSession.sessionId.withSessionNS, userSession.toJson) map (_ => userSession)
    //bucket.set[UserSession](userSession.sessionId, userSession) map (_.isSuccess)
  }

  override def findSession(sessionId: String): Future[Option[UserSession]] = {
    redis.get[String](sessionId).map(_.map(_.toUserSession))
  }

  override def registerPlayer(player: Entity): Future[Entity] = {
    val model = player.component[UserComponent].data
    val json = player.asJson()
    redis.hExists(KEY_LOGINS, model.login) flatMap {
      exists =>
        if (exists)
          throw new RuntimeException(s"User with login ${model.login} already exists!")
        redis.withTransaction {
          t =>
            t.hSet(KEY_LOGINS, model.login, model.id)
            t.set(model.id, json)
        } map (_ => player)
    }
  }

  override def stateForPlayer(userId: String): Future[Option[Entity]] =
    redis.get(userId) map {
      opt => opt.map(json => json.toEntity())
    }

  override def saveUniverse(universe: Universe, forceRestart: Boolean): Future[Boolean] = {
    val json = Universe.toJson(universe.sectors)
    if (forceRestart) {
      redis.flushDB() flatMap {
        _ =>
          redis.set(universe.label.withUniNS, json) flatMap {
            _ =>
              val admin = new Entity()
              admin.add(UserComponent(UserModel("admin-id", "admin", "Administrator", "a", "")))
              registerPlayer(admin) map (_ => true)
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

  override def loadAllPlayers(): Future[Seq[Entity]] =
    redis.hGetAll(KEY_LOGINS) flatMap {
      case Some(all) =>
        Future.sequence(all.map {
          case (_, id) => redis.get(id) map {
            case Some(u) => u.toEntity()
            case None => throw new Exception(s"User $id found in index but not in db")
          }
        }.toSeq)
      case _ => Future.successful(Seq.empty[Entity])
    }

  private def loadSession(k: String): Future[UserSession] = redis.get(k) map (_.get.toUserSession)

  override def loadAllSessions(): Future[Seq[UserSession]] = {
    redis.keys(s"$SESSION_NS:*") flatMap {
      keys =>
        Future.sequence(keys.toSeq.map(loadSession)) map {
          s =>
            Logger.info(s.toString)
            s
        }
    }
  }

  override def updateEntity(entity: Entity): Future[Boolean] = {
    val key = entity.component[StateComponent].entityKey
    redis.set(key, entity.asJson())
  }

}

