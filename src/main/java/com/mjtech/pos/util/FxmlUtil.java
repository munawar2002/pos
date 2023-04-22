package com.mjtech.pos.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

}
