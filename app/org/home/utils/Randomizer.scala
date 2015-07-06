package org.home.utils

import java.util.UUID
import scala.util.Random

object Randomizer {
  def newString(length: Int = 10): String = Random.alphanumeric.take(length).mkString

  def newLong(min: Long = 100, max: Long = 10000): Long = (Random.nextLong() % (max - min)) + min

  def newInt(min: Int = 10, max: Int = 1000): Int = (Random.nextInt() % (max - min)) + min

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
