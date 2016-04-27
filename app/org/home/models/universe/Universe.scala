package org.home.models.universe

import com.badlogic.ashley.core.Entity
import org.home.repositories.RepositoryComponent
import org.home.models.{Sector, UserSession}

import scala.concurrent.{ExecutionContext, Future}
import scalax.collection.GraphEdge.UnDiEdge
import scalax.collection.generator.{NodeDegreeRange, RandomGraph}
import scalax.collection.immutable.Graph
import scalax.collection.io.json.descriptor.{Descriptor, NodeDescriptor}
import scalax.collection._
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.edge._
import scalax.collection.edge.Implicits._
import scalax.collection.io.json._
import scalax.collection.io.json.descriptor.predefined.UnDi

case class Universe(sectors: Universe.UniverseNet, label: String)

case class FullUniverse(universe: Universe, players: Seq[Entity])



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
    val json = graph.toJson(desc)
    json
  }

  def fromJson(json: String): UniverseNet = {
    //    import scalax.collection.io.json.imp.Parser._
    //    val parsed = parse(json, desc)
    Graph.fromJson[Sector, UnDiEdge](json, desc)
  }

}

