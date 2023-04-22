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

    public boolean authenticateUser(String username, String password) throws IOException {
        Optional<User> optionalUser = userRepository.findByUsernameAndPassword(username, password);
        return optionalUser.isPresent();
    }

}
