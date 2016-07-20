package org.home.utils

object Constants {
  val KEY_LOGINS = "login-values"
  val KEY_UNIVERSE = "universe"
  val USER_NS = "u"
  val SESSION_NS = "s"
}



object SectorObjectTypes {
  val RESOURCE = 0
  val FACTORY = 1
  val PLAYER = 2
  val NPC = 3
}

object ItemType extends Enumeration {
  val SHIP = 0
  val BASE = 1
  val RES_MONEY = 2
}

object ResourceNames {
  def apply(resType: Int): String = resType match {
    case ItemType.RES_MONEY => "CREDITS"
  }
}

object SectorEnum {
}

object ActionType {
  val MOVE_SECTOR = 0
}

object ActionDuration {
  val MOVE_DURATION = 1
}
