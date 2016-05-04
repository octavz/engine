package com.home.actors.client.impl;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.home.actors.client.Service;
import com.home.actors.client.dto.LoginDTO;
import com.home.actors.game.components.*;

import java.util.HashMap;
import java.util.Map;

public class ServiceImpl implements Service {

    private final Map<String, Class<? extends Component>> playerComponentMapper = new HashMap<String, Class<? extends Component>>();
    private final Json json = new Json(JsonWriter.OutputType.json);
    private PooledEngine engine;

    public ServiceImpl(PooledEngine engine) {
        playerComponentMapper.put(QueueComponent.class.getSimpleName(), QueueComponent.class);
        playerComponentMapper.put(UserComponent.class.getSimpleName(), UserComponent.class);
        playerComponentMapper.put(ResourcesComponent.class.getSimpleName(), ResourcesComponent.class);
        playerComponentMapper.put(ChildComponent.class.getSimpleName(), ChildComponent.class);
        playerComponentMapper.put(HullComponent.class.getSimpleName(), HullComponent.class);
        playerComponentMapper.put(ItemTypeComponent.class.getSimpleName(), ItemTypeComponent.class);
        playerComponentMapper.put(LocationComponent.class.getSimpleName(), LocationComponent.class);
        playerComponentMapper.put(NamedComponent.class.getSimpleName(), NamedComponent.class);
        playerComponentMapper.put(SessionComponent.class.getSimpleName(), SessionComponent.class);
        playerComponentMapper.put(SizeComponent.class.getSimpleName(), SizeComponent.class);
        playerComponentMapper.put(SpeedComponent.class.getSimpleName(), SpeedComponent.class);
        this.engine = engine;
    }

    public PooledEngine getEngine() {
        return engine;
    }

    private Entity parse(JsonValue player) {
        Entity entity = engine.createEntity();
        for (JsonValue playerComponent : player) {
            String name = playerComponent.child.name;
            Class<? extends Component> aClass = playerComponentMapper.get(name);
            if (aClass != null) {
                JsonValue jsc = playerComponent.getChild(aClass.getSimpleName());
                Component component = json.fromJson(aClass, "{" + jsc.toString() + "}");
                entity.add(component);
            }
        }
        return entity;
    }

    private <T> Net.HttpRequest jsonRequest(T dto) {
        Net.HttpRequest request = new Net.HttpRequest("POST");
        request.setUrl("http://localhost:9000/login");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        String content = json.toJson(dto);
        request.setContent(content);
        return request;
    }

    @Override
    public void login(String login, String pass) {
        LoginDTO dto = new LoginDTO(login, pass);
        Net.HttpRequest request = jsonRequest(dto);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                JsonValue root = new JsonReader().parse(httpResponse.getResultAsString());
                engine.addEntity(parse(root.getChild("player").parent()));
                for (JsonValue item : root.getChild("items").parent()) {
                    engine.addEntity(parse(item));
                }
            }

            @Override
            public void failed(Throwable t) {
                throw new RuntimeException(t);
            }

            @Override
            public void cancelled() {
                //don't call this here
            }
        });
    }

}
