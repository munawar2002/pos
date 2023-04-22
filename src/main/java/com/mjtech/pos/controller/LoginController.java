package com.mjtech.pos.controller;

import com.mjtech.pos.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    @FXML
    private TextField usernameText;

    @FXML
    private PasswordField passwordText;

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    public void login() {
        String username = usernameText.getText();
        String password = passwordText.getText();

        userService.authenticateUser(usernameText, passwordText);
    }

}
