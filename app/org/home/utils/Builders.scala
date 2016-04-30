package org.home.utils

import com.badlogic.ashley.core.{ Entity, PooledEngine}
import org.home.game.components._

object Builders {
  private def defaultShip(parentId: String, locationComponent: LocationComponent)(implicit engine: PooledEngine): Entity = {
    val ship = engine.createEntity()
    val id = Randomizer.nextId
    ship.add(SpeedComponent(100))
    ship.add(HullComponent(1000))
    ship.add(ItemTypeComponent(ItemType.SHIP))
    ship.add(QueueComponent())
    ship.add(SizeComponent(7, 20, 5))
    ship.add(StateComponent(id))
    ship.add(NamedComponent(id, Randomizer.newName))
    ship.add(ChildComponent(parentId))
    ship.add(locationComponent)
    engine.addEntity(ship)
    ship
  }

  private def defaultBase(parentId: String, locationComponent: LocationComponent)(implicit engine: PooledEngine): Entity = {
    val base = engine.createEntity()
    val id = Randomizer.nextId
    base.add(HullComponent(10000))
    base.add(ItemTypeComponent(ItemType.BASE))
    base.add(QueueComponent())
    base.add(SizeComponent(700, 2000, 500))
    base.add(StateComponent(id))
    base.add(NamedComponent(id, Randomizer.newName))
    base.add(ChildComponent(parentId))
    base.add(locationComponent)
    engine.addEntity(base)
    base
  }

  def createNewPlayer(model: UserComponent)(implicit engine: PooledEngine): (Entity, Seq[Entity]) = {
    val newPlayer = engine.createEntity()
    newPlayer.add(StateComponent(model.id))
    newPlayer.add(QueueComponent())
    newPlayer.add(model)
    newPlayer.add(ResourcesComponent(1000L))
    val ship = defaultShip(model.id, LocationComponent(model.startSector, Vector3D.create()))
    val base = defaultBase(model.id, LocationComponent(model.startSector, Vector3D.create()))
    engine.addEntity(newPlayer)
    (newPlayer, Seq(ship, base))
  }

}
