package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.LoginHandler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class LoginController {

    @FXML
    private TextField usernameText;

    @FXML
    private PasswordField passwordText;

    private final LoginHandler loginHandler;

    public LoginController(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    public void login() throws IOException {
        loginHandler.authenticateUser(usernameText, passwordText);
    }

}
