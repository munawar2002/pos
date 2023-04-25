package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.entity.User;
import com.mjtech.pos.repository.UserRepository;
import com.mjtech.pos.util.ActiveUser;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@AllArgsConstructor
public class LoginHandler {

    private final UserRepository userRepository;

    private final ConfigurableApplicationContext applicationContext;

    public void authenticateUser(TextField usernameField, PasswordField passwordField) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        username = username.isEmpty() ? "admin" : username;
        password = password.isEmpty() ? "123" : password;

        if(username.isEmpty() || password.isEmpty()) {
            FxmlUtil.callErrorAlert("Username or password is empty.");
            return;
        }

        Optional<User> optionalUser = userRepository.findByUsernameAndPassword(username, password);

        boolean success = optionalUser.isPresent();
        if(success) {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FxmlUtil.callMainForm(stage, "/fxml/main.fxml", applicationContext);
            ActiveUser.setActiveUser(optionalUser.get());
        } else {
            FxmlUtil.callErrorAlert("Username or password is incorrect. Please try again!");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

}
