package com.mjtech.pos.util;

import com.mjtech.pos.GuiHandler.GenericFormHandler;
import com.mjtech.pos.controller.GenericFormController;
import com.mjtech.pos.controller.PopupController;
import com.mjtech.pos.dto.GenericFromDto;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class FxmlUtil {

    public void callMainForm(Stage primaryStage, String formName, ConfigurableApplicationContext context) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FxmlUtil.class.getResource(formName));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void callForm(Stage stage, String formName, ConfigurableApplicationContext context) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FxmlUtil.class.getResource(formName));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void callGenericForm(Stage stage, String formName, GenericFormController controller, ConfigurableApplicationContext context) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FxmlUtil.class.getResource(formName));
        loader.setControllerFactory(context::getBean);
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void callPopupForm(GenericFormHandler handler, PopupController controller, ConfigurableApplicationContext context) {
        TextField field = new TextField();
        field.setText(controller.getValueToSearch());
        List<GenericFromDto> genericFromDtos = handler.searchEntity(controller.getEntityName(), controller.getValueToSearch());
        if(genericFromDtos.size() == 1) {
            controller.setSelectedEntity(genericFromDtos.get(0));
            return;
        }
        callPopupForm(controller, context);
    }

    private void callPopupForm(PopupController controller, ConfigurableApplicationContext context) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FxmlUtil.class.getResource("/fxml/entityPopup.fxml"));
            loader.setControllerFactory(context::getBean);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed while opening popup form for %s", controller.getEntityName()), e);
        }
    }

    public void callErrorAlert(String message) {
        callAlert(Alert.AlertType.ERROR, "Error", message);
    }

    public void callWarningAlert(String message) {
        callAlert(Alert.AlertType.WARNING, "Warning", message);
    }

    public void callAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean callConfirmationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

}
