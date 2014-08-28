package org.home.actors.org.home.utils

import java.util.UUID
import scala.util.Random

/**
 * Created by octav on 28.08.2014.
 */
object Randomizer {

  val timeSpanOffset = 10

  /**
   * @return random string id
   */
  def newId = UUID.randomUUID().toString

  /**
   * @return a random time span in seconds
   */
  def newTimeSpan = Random.nextLong() + 10

}
