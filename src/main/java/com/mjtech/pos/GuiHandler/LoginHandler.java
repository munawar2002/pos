package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.entity.Role;
import com.mjtech.pos.entity.User;
import com.mjtech.pos.entity.UserRole;
import com.mjtech.pos.repository.RoleRepository;
import com.mjtech.pos.repository.UserRoleRepository;
import com.mjtech.pos.service.TerminalService;
import com.mjtech.pos.service.UserService;
import com.mjtech.pos.util.ActiveUser;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class LoginHandler {

    private final UserService userService;
    private final UserRoleRepository userRoleRepository;

    private final TerminalService terminalService;

    private final ConfigurableApplicationContext applicationContext;

    public void authenticateUser(TextField usernameField, PasswordField passwordField) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // username = username.isEmpty() ? "admin" : username;
        // password = password.isEmpty() ? "123" : password;

        if(username.isEmpty() || password.isEmpty()) {
            FxmlUtil.callErrorAlert("Username or password is empty.");
            return;
        }

        User user = userService.findUserWithRoles(username, password);

        //Terminal currentTerminal = terminalService.findCurrentTerminal();
//        if(currentTerminal == null) {
//            // TODO: show create new terminal form. It would take only computer name, expiryDate
//            FxmlUtil.callErrorAlert("Your computer is not registered in our system.");
//            Platform.exit();
//            return;
//        }
//
//        ActiveTerminal.setTerminal(currentTerminal);

        if(user!= null) {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FxmlUtil.callMainForm(stage, "/fxml/main.fxml", applicationContext);
            List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
            List<Role> roles = userRoles.stream().map(UserRole::getRole).collect(Collectors.toList());
            user.setRoles(roles);
            ActiveUser.setActiveUser(user);
        } else {
            FxmlUtil.callErrorAlert("Username or password is incorrect. Please try again!");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

}
