package org.home.models

import org.home.models.Universe.UniverseNet
import org.home.utils.Randomizer

import scalax.collection.GraphEdge.UnDiEdge
import scalax.collection.edge.LUnDiEdge
import scalax.collection.generator.{NodeDegreeRange, RandomGraph}
import scalax.collection.immutable.Graph

case class Universe(sectors: UniverseNet, label: String) {

}

object Universe extends Createable[Universe] {
  type UniverseNet = Graph[Sector, UnDiEdge]

  override def create(): Universe = {
    //    val sectors = List.range(1, 10).map(_ => Sector.create())

    val randomGraphGen =
      RandomGraph[Sector, UnDiEdge, Graph](
        Graph, new RandomGraph.Metrics[Sector] {
          def nodeGen: Sector = Sector.create()

          override def order: Int = 10

          override def nodeDegrees: NodeDegreeRange = NodeDegreeRange(2, 4, uniform = false)
        },
        Set(UnDiEdge, LUnDiEdge))
    val randomGraph = randomGraphGen.draw
    randomGraph.nodes.foreach{
      n =>
        println(n.value)
        println(n.neighbors)
    }
    Universe(sectors = randomGraph, label = Randomizer.newName)
  }

}

