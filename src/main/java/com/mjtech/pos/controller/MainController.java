package com.mjtech.pos.controller;

import com.mjtech.pos.util.FxmlUtil;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private Stage secondaryStage;

    public void newOrderForm() {
        System.out.println("----_ Selected new order form");
    }

    public void productForm() throws IOException {
        secondaryStage = new Stage();
        FxmlUtil.callForm(secondaryStage, "/fxml/product.fxml");
    }

    public void closeApplication() {
        Platform.exit();
    }

}
