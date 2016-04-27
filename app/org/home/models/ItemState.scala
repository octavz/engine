package org.home.models

import org.home.game.components.LocationComponent

case class ItemState(id: String
                     , name: String
                     , itemType: Int
                     , props: Map[String, String]
                     , location: LocationComponent
                     , qu: ActionQu) extends GenericState {
}





