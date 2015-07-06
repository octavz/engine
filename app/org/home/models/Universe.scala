package org.home.models

import org.home.utils.Randomizer

case class Universe(sectors: List[Sector], label: String)

object Universe extends Createable[Universe] {

  override def create(): Universe = {
    val sectors = List.range(1, 10).map(_ => Sector.create())
    Universe(sectors = sectors, label = Randomizer.newString())
  }

}

