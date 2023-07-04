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

    public void productForm() throws IOException {
        Stage productStage = new Stage();
        FxmlUtil.callForm(productStage, "/fxml/product.fxml", applicationContext);
    }

    public void productCategoryForm() throws IOException {
        GenericFormController genericFormController = applicationContext.getBean(GenericFormController.class);
        genericFormController.setFormName(GenericFormValue.PRODUCT_CATEGORY.getValue());

        Stage productCategoryStage = new Stage();
        FxmlUtil.callGenericForm(productCategoryStage, "/fxml/genericForm.fxml", genericFormController, applicationContext);
    }

    public void receptionOrderForm() throws IOException {
        Stage receptionOrderStage = new Stage();
        FxmlUtil.callForm(receptionOrderStage, "/fxml/receptionOrder.fxml", applicationContext);
    }

    public void refundOrderForm() throws IOException {
        Stage refundOrderStage = new Stage();
        FxmlUtil.callForm(refundOrderStage, "/fxml/refundOrder.fxml", applicationContext);
    }

    public void salesForm() throws IOException {
        Stage salesFormStage = new Stage();
        FxmlUtil.callForm(salesFormStage, "/fxml/searchInvoice.fxml", applicationContext);
    }

    public void productCompanyForm() throws IOException {
        Stage productCompanyForm = new Stage();
        FxmlUtil.callForm(productCompanyForm, "/fxml/productCompany.fxml", applicationContext);
    }

    public void closeApplication() {
        Platform.exit();
    }

}
