package com.mjtech.pos.GuiHandler;

import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class ProductHandler {

    public void saveProduct(TextField codeTextField,
                            TextField nameTextField,
                            TextField categoryTextField,
                            TextField companyTextField,
                            TextField supplierTextField,
                            TextField buyPriceTextField,
                            TextField sellPriceTextField) {
        String code = codeTextField.getText();
        String name = nameTextField.getText();
        String category = null;
        String company = companyTextField.getText();
        String supplier = supplierTextField.getText();
        String buyPrice = buyPriceTextField.getText();
        String sellPrice = sellPriceTextField.getText();

    }

    public void editProduct() {

    }

    public void deleteProduct() {

    }

    public void searchProduct() {

    }

    public void browseImage() {

    }

}
