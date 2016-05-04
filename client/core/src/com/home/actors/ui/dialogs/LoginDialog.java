package com.home.actors.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.home.actors.client.Service;

public class LoginDialog extends Dialog {

    private final Stage stage;
    private final TextField textLogin;
    private final TextField textPass;
    private final Label lblMessage;
    private final Service service;

    public LoginDialog(Stage stage, Skin skin, Service service) {
        super("Login", skin);
        this.stage = stage;
        this.service = service;
        textLogin = new TextField("test", skin);
        textPass = new TextField("test", skin);
        textPass.setPasswordMode(true);
        textPass.setPasswordCharacter('*');
        lblMessage = new Label("", skin);
        init();
    }

    private void init() {
        Table contentTable = getContentTable();
        contentTable.add(new Label("Enter login/password", getSkin())).colspan(2);
        contentTable.row();
        contentTable.add(new Label("Login: ", getSkin())).align(Align.right);
        contentTable.add(textLogin).width(200);
        contentTable.row();
        contentTable.add(new Label("Password: ", getSkin())).align(Align.right);
        contentTable.add(textPass).width(200);
        contentTable.row();
        contentTable.add(lblMessage).colspan(2);
        button("Login", true); //sends "true" as the result
//        dlgLogin.button("Cancel", false);  //sends "false" as the result
        key(66, true); //sends "true" when the ENTER key is pressed
        pack();
        stage.setKeyboardFocus(textLogin);
    }

    private String validateLogin(String user, String pass) {
        if (user.length() > 0 && pass.length() > 0) return "";
        else return "User or password is not sent";
    }

    public void result(Object obj) {
        Boolean result = (Boolean) obj;
        if (result) {
            String login = textLogin.getText();
            String pass = textPass.getText();
            String valid = validateLogin(login, pass);
            if (valid.length() == 0) {
                service.login(login, pass);
                this.remove();
            } else {
                this.cancel();
                lblMessage.setText(valid);
                this.show(stage);
            }
        }
    }
}
