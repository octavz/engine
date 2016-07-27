package com.home.actors

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.badlogic.gdx.graphics.{GL20, Texture}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.home.actors.client.Service
import com.home.actors.client.impl.ServiceImpl
import com.home.actors.client.ui.dialogs.LoginDialog

class Main extends ApplicationAdapter {
  private lazy val batch = new SpriteBatch()
  private lazy val view = new ScreenViewport
  private lazy val stage = new Stage(view)
  private lazy val skin = new Skin(Gdx.files.internal("data/uiskin.json"))
  private lazy val dlgLogin = new LoginDialog(stage, skin, service)
  private val engine = new PooledEngine
  private val service = new ServiceImpl(engine)

  override def create() {
    Gdx.input.setInputProcessor(stage)
    dlgLogin.show(stage)
  }


  override def resize(width: Int, height: Int) {
    stage.getViewport.update(width, height, true)
  }

  override def render() {
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    batch.begin()
    batch.end()
    stage.draw()
  }

  override def dispose() {
    skin.dispose()
    stage.dispose()
  }
}
