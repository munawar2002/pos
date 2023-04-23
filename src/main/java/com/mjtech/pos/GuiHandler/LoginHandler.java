package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.service.UserService;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class LoginHandler {

    private final UserService userService;

    public void authenticateUser(TextField usernameField, PasswordField passwordField) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        username = username.isEmpty() ? "admin" : username;
        password = password.isEmpty() ? "123" : password;

        if(username.isEmpty() || password.isEmpty()) {
            FxmlUtil.callErrorAlert("Username or password is empty.");
            return;
        }

        boolean success = userService.authenticateUser(username, password);

        if(success) {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FxmlUtil.callMainForm(stage, "/fxml/main.fxml");
        } else {
            FxmlUtil.callErrorAlert("Username or password is incorrect. Please try again!");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

}
