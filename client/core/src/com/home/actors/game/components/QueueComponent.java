package com.home.actors.game.components;

import com.badlogic.ashley.core.Component;
import com.home.actors.game.models.PlayerAction;

import java.util.List;


public class QueueComponent implements Component{
    List<PlayerAction> content;
}
