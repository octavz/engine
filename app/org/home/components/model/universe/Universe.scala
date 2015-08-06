package org.home.components.model.universe

import org.home.utils.Randomizer

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

  def toJson = Universe.toJson(sectors)

}

object Universe extends Createable[Universe] {
  type UniverseNet = Graph[Sector, UnDiEdge]

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

    Universe(sectors = net, label = Randomizer.newName)
  }

  def toJson(graph: UniverseNet): String = {
    val sectorDescriptor = new NodeDescriptor[Sector](typeId = "Sectors") {
      def id(node: Any) = node match {
        case Sector(id, _, _) => id
      }
    }
    val desc = new Descriptor[Sector](
      defaultNodeDescriptor = sectorDescriptor,
      defaultEdgeDescriptor = UnDi.descriptor[Sector]())
    val json = graph.toJson(desc)
    println(Graph.fromJson[Sector, UnDiEdge](json, desc))
    json
  }

}

