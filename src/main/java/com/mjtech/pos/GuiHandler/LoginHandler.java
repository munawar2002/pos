package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.service.UserService;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.Alert;
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

        boolean success = userService.authenticateUser(username, password);

        if(success) {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FxmlUtil.callForm(stage, "/fxml/main.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failure");
            alert.setContentText("Username or password is incorrect. Please try again!");
            alert.showAndWait();
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

}
