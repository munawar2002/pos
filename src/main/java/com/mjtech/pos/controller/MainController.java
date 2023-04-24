package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.LoginHandler;
import com.mjtech.pos.constant.GenericFormValue;
import com.mjtech.pos.util.FxmlUtil;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class MainController {

    private Stage secondaryStage;

    private final ConfigurableApplicationContext applicationContext;

    private final LoginHandler loginHandler;

    public MainController(LoginHandler loginHandler, ConfigurableApplicationContext applicationContext) {
        this.loginHandler = loginHandler;
        this.applicationContext = applicationContext;
    }


    public void newOrderForm() {
        System.out.println("----_ Selected new order form");
    }

    public void productForm() throws IOException {
        secondaryStage = new Stage();
        FxmlUtil.callForm(secondaryStage, "/fxml/product.fxml", applicationContext);
    }

    public void productCategoryForm() throws IOException {
        GenericFormController genericFormController = applicationContext.getBean(GenericFormController.class);
        genericFormController.setFormName(GenericFormValue.PRODUCT_CATEGORY.getValue());

        secondaryStage = new Stage();
        FxmlUtil.callGenericForm(secondaryStage, "/fxml/genericForm.fxml", genericFormController, applicationContext);
    }


    public void closeApplication() {
        Platform.exit();
    }

}
