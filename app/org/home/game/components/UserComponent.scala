package org.home.game.components

import com.badlogic.ashley.core.Component
import org.home.utils.ScenarioType

case class UserComponent(id: String
                         , login: String
                         , name: String
                         , var password: String
                         , startSector: String
                         , scenario: Int = ScenarioType.NORMAL) extends Component



