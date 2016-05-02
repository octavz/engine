package com.home.actors;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.home.actors.client.Service;
import com.home.actors.client.impl.ServiceImpl;
import com.home.actors.ui.dialogs.LoginDialog;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture img;
    private Stage stage;
    private Skin skin;
    private Dialog dlgLogin;
    private final Service service = new ServiceImpl();

    @Override
    public void create() {
        ScreenViewport view = new ScreenViewport();
        stage = new Stage(view);
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Dialog loginDialog = createLoginDialog();
        loginDialog.show(stage);
    }

    private Dialog createLoginDialog() {
        if (dlgLogin == null) dlgLogin = new LoginDialog(stage, skin, service);
        return dlgLogin;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
        stage.draw();
    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
