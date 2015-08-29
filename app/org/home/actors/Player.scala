package org.home.actors

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import org.home.actors.messages._
import org.home.models.universe.SectorPosition
import org.home.utils.Randomizer._
import play.api.Logger
import play.api.Logger._

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import org.home.models._

object Player {
  def props(state: PlayerState): Props = Props(new Player(state))
}

class Player(var state: PlayerState) extends Actor with ActorLogging {
  val items: ListBuffer[String] = ListBuffer.empty
  implicit val askTimeout = Timeout(2.second)

  def newItem(itemType: Int, props: Map[String, String]): Option[String] = {
    try {
      val newItemId = nextId
      context.actorOf(PlayerItem.props(
        ItemState(
          id = newItemId
          , itemType = itemType
          , name = newRoman()
          , props = Map.empty)), name = nextId)
      items += newItemId
      Some(newItemId)
    } catch {
      case e: Throwable =>
        logger.error("newItem", e)
        None
    }
  }

  def restore(playerState: PlayerState) = {
    state = playerState
    playerState.items.foreach {
      s =>
        context.actorOf(PlayerItem.props(s), name = s.id)
        items += s.id
    }
  }

  def getState: Future[Either[String, PlayerState]] = {
    //return state here
    val childrenState = Future.sequence(context.children.toList.map {
      c =>
        val cf = c ? State
        cf map {
          case x: ItemState => Right(x)
          case _ =>
            logger.error(s"Cannot get state for ${c.path}")
            Left(s"Cannot get state for ${c.path}")
        }
    })

    val f = childrenState map {
      lst =>
        Right(PlayerState(
          owner = state.owner
          , qu = state.qu
          , startSector = state.startSector
          , items = lst.filterNot(_.isLeft).map(_.right.get)))
    }

    f recover {
      case e: Throwable =>
        Logger.logger.error("getPlayerState", e)
        Left(e.getMessage)
    }
  }

  def receive = {
    case NewPlayerItem(itemType, props) =>
      val rep = newItem(itemType, props) match {
        case Some(id) => id
        case _ => Error
      }
      sender ! rep
    case Info => sender ! state.owner
    case State => getState.pipeTo(sender())
    case MoveShipInSector(SectorPosition(x, y, z)) =>

    case Tic => log.info("Received tic...")
    case x => log.info("Player received unknown message: " + x)
  }

}

