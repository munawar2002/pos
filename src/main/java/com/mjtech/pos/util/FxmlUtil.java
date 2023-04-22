package com.mjtech.pos.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class FxmlUtil {

    public void callForm(Stage primaryStage, String formName) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FxmlUtil.class.getResource(formName));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void callErrorAlert(String message) {
        callAlert(Alert.AlertType.ERROR, "Error", message);
    }

    public void callAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
