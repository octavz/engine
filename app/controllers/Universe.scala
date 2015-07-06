package controllers

import org.home.models.Sector

object Universe {

  def generate(size: Int = 10): List[Sector] = Range(1, size).map(_ => Sector.create()).toList


}
