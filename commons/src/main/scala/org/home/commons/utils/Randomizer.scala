package org.home.commons.utils

import java.util.UUID

import org.home.commons.models.Sector
import org.home.commons.models.universe.Universe

import scala.util.Random

object Randomizer {

  val rawNames = List("Puscuobos", "Tuslaethea", "Utrorix", "Iostrinda", "Noelea",
    "Yaynia", "Crufonus", "Flufurus", "Crurn", "Skolla", "Robroyrus", "Topliorilia",
    "Faspiuq", "Toshilles", "Foyliv", "Vievis", "Prabonov", "Slecagantu", "Brara",
    "Strao", "Kufluathea", "Dutheunides", "Mastorix", "Teflides", "Fopra", "Huoyama",
    "Frabacarro", "Snecapra", "Swichi", "Brilles", "Kufluathea", "Dutheunides", "Mastorix",
    "Teflides", "Fopra", "Huoyama", "Frabacarro", "Snecapra", "Swichi", "Brilles",
    "Wecruirilia", "Jugleymia", "Askyke", "Bewheron", "Oilara", "Rouliv", "Stuferia",
    "Glufacury", "Stars", "Shonoe", "Towhiyter", "Focrahiri", "Ruskinda", "Lesmov",
    "Seilea", "Ienus", "Chufunerth", "Skodomia", "Brao", "Blarth", "Pugroymia",
    "Nusnainus", "Jatrypso", "Acliea", "Ietune", "Wiunides", "Scufetune", "Grufenia",
    "Sleshan", "Shyria", "Meskunus", "Pacluter", "Gesnara", "Qocheshan", "Uyhines",
    "Doerilia", "Drabanov", "Snecoria", "Plides", "Frora", "Rasliylara", "Ogruaruta",
    "Fograo", "Veblorix", "Azuno", "Xuenides", "Smufophus", "Stodunia", "Gladus",
    "Brion", "Totroezuno", "Jaswoenides", "Abrolla", "Beprippe", "Piytis", "Goylia",
    "Scufunides", "Prufarus", "Broria", "Flagua", "Mudbourne", "Windpond", "Bayfair",
    "Stillpost", "Eaglestorm", "Chillvalley", "Blindshear", "Basinmoor", "Mageguard", "Slygulch",
    "Oldborn", "Deerdenn", "Elderfall", "Spiritshade", "Lostmire", "Littlefair", "Cliffwater",
    "Snakewood", "Grimeland", "Mudtide", "Lastgarde", "Nightvalley", "Silentfrost", "Everpoint",
    "Starhold", "Sleekband", "Edgefort", "Castlemore", "Shroudrock", "Windmaw", "Deadrun",
    "Silverfalls", "Oldguard", "Bridgewell", "Flatvein", "Redgulch", "Grimelight", "Northpond",
    "Goldstar", "Stagguard", "Springshade", "Windsummit", "Summercliff", "Littleacre", "Deadfair",
    "Pondborn", "Chillharbor", "Steelwall", "Neverbay", "Oxhallow", "Oldpeak", "Magetown",
    "Shadowpass", "Westville", "Beachwind", "Hazelwater", "Stillbay", "Rimebreach", "Darkguard",
    "Demonreach", "Cavestar", "Stormyard", "Puredrift", "Redreach", "Ebonshell", "Drydale",
    "Houndpost", "Thornwood", "Duskhallow", "Crystalharbor", "Winterspire", "Pondlight", "Sleetreach",
    "Rivermoor", "Hazelstrand", "Greenhand", "Midmaw", "Stagbury", "Redhill", "Oceanmeadow")

  val names = rawNames.distinct.sorted

  def newString(length: Int = 10): String = Random.alphanumeric.take(length).mkString

  def newName: String = {
    val rand = newInt(0, names.size - 1)
    names(rand)
  }

  import scala.reflect._

  def newNumeric[T <: AnyVal : ClassTag](): T = {
    if (classTag[T].runtimeClass == classOf[Int]) newInt().asInstanceOf[T]
    else if (classTag[T].runtimeClass == classOf[Long]) newLong().asInstanceOf[T]
    else throw new Exception(s"Random not implemented for type ${classTag[T].runtimeClass.getName}")
  }

  def newLong(min: Long = 100, max: Long = 10000): Long = 
    (Math.abs(Random.nextLong()) % (max - min)) + min

  def newInt(min: Int = 10, max: Int = 1000): Int =
    min + Random.nextInt((max - min) + 1)

  def newFromEnum(enum: Enumeration): enum.Value =
    enum(newInt(0, enum.values.size - 1))

  val timeSpanOffset = 10

  def someSector(universe: Universe): Sector = {
    val nodes = universe.sectors.nodes.map(_.value.asInstanceOf[Sector])
    nodes.drop(Randomizer.newInt(0, nodes.size)).head
  }

  /**
    * @return random string id
    */
  def newId: String = UUID.randomUUID().toString

  def nextId: String = newString(12).toLowerCase

  /**
    * @return a random time span in seconds
    */
  def newTimeSpan: Long = Random.nextLong() + 10

  def newRoman(): String = toRomanNumerals(newInt())

  private def toRomanNumerals(number: Int): String = {
    toRomanNumerals(number, List(
      ("M", 1000), ("CM", 900), ("D", 500),
      ("CD", 400), ("C", 100), ("XC", 90),
      ("L", 50), ("XL", 40), ("X", 10), ("IX",
        9), ("V", 5), ("IV", 4), ("I", 1)))
  }

  private def toRomanNumerals(number: Int, digits: List[(String, Int)]): String = digits match {
    case Nil => ""
    case h :: t => h._1 * (number / h._2) + toRomanNumerals(number % h._2, t)
  }
}
