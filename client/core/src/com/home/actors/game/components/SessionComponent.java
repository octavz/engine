package com.home.actors.game.components;

import com.badlogic.ashley.core.Component;
import com.home.actors.game.models.UserSession;

public class SessionComponent implements Component {
    public String userId;
    public String sessionId;
}
