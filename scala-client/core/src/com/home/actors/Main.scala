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
  private var batch: SpriteBatch = _
  private var img: Texture = _
  private var stage: Stage = _
  private var skin: Skin = _
  private var dlgLogin: Dialog = _
  final private val engine: PooledEngine = new PooledEngine
  final private val service: Service = new ServiceImpl(engine)

  override def create() {
    val view: ScreenViewport = new ScreenViewport
    stage = new Stage(view)
    Gdx.input.setInputProcessor(stage)
    batch = new SpriteBatch
    img = new Texture("badlogic.jpg")
    skin = new Skin(Gdx.files.internal("data/uiskin.json"))
    //        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    //        pixmap.setColor(Color.WHITE);
    //        pixmap.fill();
    val loginDialog: Dialog = createLoginDialog
    loginDialog.show(stage)
  }

  private def createLoginDialog: Dialog = {
    if (dlgLogin == null) dlgLogin = new LoginDialog(stage, skin, service)
    dlgLogin
  }

  override def resize(width: Int, height: Int) {
    stage.getViewport.update(width, height, true)
  }

  override def render() {
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    batch.begin()
    batch.draw(img, 0, 0)
    batch.end()
    stage.draw()
  }

  override def dispose() {
    skin.dispose()
    stage.dispose()
  }
}
