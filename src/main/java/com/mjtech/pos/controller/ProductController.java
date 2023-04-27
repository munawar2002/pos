package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.GenericFormHandler;
import com.mjtech.pos.GuiHandler.ProductHandler;
import com.mjtech.pos.constant.GenericFormValue;
import com.mjtech.pos.dto.GenericFromDto;
import com.mjtech.pos.util.FxmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class ProductController implements Initializable {

    @Autowired
    private ProductHandler productHandler;

    @Autowired
    private GenericFormHandler genericFormHandler;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @FXML
    private TextField codeTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField categoryTextField;

    @FXML
    private TextField companyTextField;

    @FXML
    private TextField buyPriceTextField;

    @FXML
    private TextField sellPriceTextField;

    @FXML
    private TextField supplierTextField;

    @FXML
    private TextField codeSearchTextField;

    @FXML
    private TextField nameSearchTextField;

    @FXML
    private TextField categorySearchTextField;

    @FXML
    private TextField companySearchTextField;

    @FXML
    private TextField buyPriceSearchTextField;

    @FXML
    private TextField sellPriceSearchTextField;

    @FXML
    private TextField supplierSearchTextField;

    public GenericFromDto selectedCategory;

    public GenericFromDto getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(GenericFromDto selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public void saveBtn() {
        productHandler.saveProduct(codeTextField, nameTextField, categoryTextField,
                companyTextField, supplierTextField, buyPriceTextField, sellPriceTextField);
    }

    public void editBtn() {
        System.out.println("-----selected Category id "+ selectedCategory.getId() + "--" +selectedCategory.getName());
    }

    public void clearBtn() {

    }

    public void deleteBtn() {

    }

    public void browseBtn() {

    }

    public void searchBtn() {

    }

    public void categoryPopup() {
        PopupController controller = applicationContext.getBean(PopupController.class);
        controller.setEntityName(GenericFormValue.PRODUCT_CATEGORY.getValue());
        controller.setValueToSearch(categoryTextField.getText());
        FxmlUtil.callPopupForm(genericFormHandler, controller, applicationContext);
        categoryTextField.setText(controller.getSelectedEntity().getName());
        this.selectedCategory = controller.getSelectedEntity();
        companyTextField.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



    }
}
