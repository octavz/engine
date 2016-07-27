package com.home.actors.client.ui.dialogs

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.utils.Align
import com.home.actors.client.Service

class LoginDialog(val stage: Stage, val skin: Skin, val service: Service) extends Dialog("Login", skin) {
  private val textLogin = new TextField("test", skin)
  private val textPass = new TextField("test", skin)
  textPass.setPasswordMode(true)
  textPass.setPasswordCharacter('*')
  val lblMessage = new Label("", skin)
  init()

  private def init() {
    val contentTable: Table = getContentTable
    contentTable.add(new Label("Enter login/password", getSkin)).colspan(2)
    contentTable.row
    contentTable.add(new Label("Login: ", getSkin)).align(Align.right)
    contentTable.add(textLogin).width(200)
    contentTable.row
    contentTable.add(new Label("Password: ", getSkin)).align(Align.right)
    contentTable.add(textPass).width(200)
    contentTable.row
    contentTable.add(lblMessage).colspan(2)
    button("Login", true) //sends "true" as the result
    //        dlgLogin.button("Cancel", false);  //sends "false" as the result
    key(66, true) //sends "true" when the ENTER key is pressed
    pack()
    stage.setKeyboardFocus(textLogin)
  }

  private def validateLogin(user: String, pass: String): String = if (user.length > 0 && pass.length > 0) ""
  else "User or password is not sent"

  override def result(obj: Any) {
    val result: Boolean = obj.asInstanceOf[Boolean]
    if (result) {
      val login: String = textLogin.getText
      val pass: String = textPass.getText
      val valid: String = validateLogin(login, pass)
      if (valid.length == 0) {
        service.login(login, pass)
        this.remove
      }
      else {
        this.cancel()
        lblMessage.setText(valid)
        this.show(stage)
      }
    }
  }
}
