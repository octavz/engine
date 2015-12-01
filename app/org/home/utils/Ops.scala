package org.home.utils

import org.home.models.universe.SectorPosition

object Ops {

  def sqr(v: Long) = Math.pow(v, 2)

  def dist(p1: SectorPosition, p2: SectorPosition): Double = {
    Math.sqrt(sqr(p1.x - p2.x) + sqr(p1.y - p2.y) - sqr(p1.z - p2.z))
  }
}
