package org.home.models

import org.home.utils.ScenarioType

case class UserModel(id: String, login: String, name: String, var password: String, startSector: String, scenario: Int = ScenarioType.NORMAL)
