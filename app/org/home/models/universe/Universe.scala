package org.home.models.universe

import org.home.components.RepositoryComponent
import org.home.utils.Randomizer

import scala.concurrent.{ExecutionContext, Future}
import scalax.collection.GraphEdge.UnDiEdge
import scalax.collection.generator.{NodeDegreeRange, RandomGraph}
import scalax.collection.immutable.Graph
import scalax.collection.io.json.descriptor.{Descriptor, NodeDescriptor}
import scalax.collection.io.json.descriptor.predefined.UnDi

import scalax.collection._
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._
import scalax.collection.edge._, scalax.collection.edge.Implicits._

import scalax.collection.io.json._

case class Universe(sectors: Universe.UniverseNet, label: String) {

}

class UniverseService {
  this: RepositoryComponent =>

  def loadUniverse(forceRestart: Boolean = false): Future[Universe] = {

    implicit val ec = ExecutionContext.global
    def createNew = {
      val u = Universe.create()
      repository.saveUniverse(u) map {
        case true => u
        case _ => throw new RuntimeException("Universe cannot be created")
      }
    }

    if (forceRestart) {
      createNew
    } else {
      repository.loadUniverse("main") flatMap {
        case Some(u) => Future.successful(u)
        case _ => createNew
      }
    }
  }

  def saveUniverse(universe: Universe) = repository.saveUniverse(universe)

}

object Universe extends Createable[Universe] {

  type UniverseNet = Graph[Sector, UnDiEdge]

  val sectorDescriptor = new NodeDescriptor[Sector](typeId = "Sectors") {
    def id(node: Any) = node match {
      case Sector(id, _, _) => id
    }
  }

  val desc = new Descriptor[Sector](
    defaultNodeDescriptor = sectorDescriptor,
    defaultEdgeDescriptor = UnDi.descriptor[Sector]())

  override def create(): Universe = {

    val randomGraphGen = RandomGraph.unDiGraph(
      Graph,
      new RandomGraph.Metrics[Sector] {
        def nodeGen: Sector = Sector.create()

        override def order: Int = 10

        override def nodeDegrees: NodeDegreeRange = NodeDegreeRange(2, 4, uniform = false)
      })

    val net = randomGraphGen.draw

    net.nodes.foreach {
      n =>
        println(n.value)
        println(n.neighbors)
    }

    Universe(sectors = net, label = "main")
  }

  def toJson(graph: UniverseNet): String = {
    graph.toJson(desc)
  }

  def fromJson(json: String): UniverseNet = {
    Graph.fromJson[Sector, UnDiEdge](json, desc)
  }

}
