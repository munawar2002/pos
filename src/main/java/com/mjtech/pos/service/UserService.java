package com.mjtech.pos.service;

import com.mjtech.pos.entity.User;
import com.mjtech.pos.repository.UserRepository;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean authenticateUser(TextField usernameField, PasswordField passwordField) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isPresent() && username.equals(optionalUser.get().getUsername())
                && password.equals(optionalUser.get().getPassword())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Login Successfull!!!");
            alert.showAndWait();
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
        return true;
    }

}
