package com.home.actors.client.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.home.actors.client.Service;
import com.home.actors.client.dto.LoginDTO;

public class ServiceImpl implements Service {
    @Override
    public void login(String login, String pass) {
        Net.HttpRequest request = new Net.HttpRequest("POST");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        Json json = new Json(JsonWriter.OutputType.json);
        LoginDTO dto = new LoginDTO();
        dto.setLogin(login);
        dto.setPassword(pass);
        String content = json.toJson(dto);
        request.setContent(content);
        request.setUrl("http://localhost:9000/login");
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                System.out.println(httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {

            }

            @Override
            public void cancelled() {

            }
        });
    }

}
