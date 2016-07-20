package org.home.commons.game.components

import com.badlogic.ashley.core.Component
import org.home.commons.utils.Vector3D


case class LocationComponent(var sectorId: String, var sectorPosition: Vector3D) extends Component



