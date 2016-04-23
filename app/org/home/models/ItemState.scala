package org.home.models

import org.home.models.universe.UniverseLocation

case class ItemState(id: String
                     , name: String
                     , itemType: Int
                     , props: Map[String, String]
                     , location: UniverseLocation
                     , qu: ActionQu) extends GenericState {
}





