package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.GenericFormHandler;
import com.mjtech.pos.GuiHandler.ProductHandler;
import com.mjtech.pos.constant.GenericFormValue;
import com.mjtech.pos.dto.GenericFromDto;
import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.util.FxmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
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
    private TextField quantityTextField;

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

    @FXML
    private TextField quantitySearchTextField;

    @FXML
    private CheckBox isServiceCheckbox;

    @FXML
    private TableView<ProductDto> productTable;

    @FXML
    private ImageView productImage;

    private GenericFromDto selectedCategory;

    private GenericFromDto selectedSupplier;

    private GenericFromDto selectedCompany;

    private GenericFromDto selectedSearchCategory = new GenericFromDto();

    private GenericFromDto selectedSearchSupplier = new GenericFromDto();

    private GenericFromDto selectedSearchCompany = new GenericFromDto();

    private ProductDto selectedProduct;

    private FileChooser fileChooser = new FileChooser();


    public GenericFromDto getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(GenericFromDto selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public void saveBtn() {
        var inputMap = Map.of(codeTextField, "Product code",
               nameTextField, "Product name",
               sellPriceTextField, "Product Sell Price",
               quantityTextField, "Product Quantity" );
        String res = FxmlUtil.validateEmptyTextFields(inputMap);
        if(res != null) {
            FxmlUtil.callErrorAlert(res);
            return;
        }

        var inputMapIds = Map.of("Product Category", selectedCategory.getId(),
                "Product Supplier", selectedSupplier.getId(),
                "Product Company", selectedCompany.getId());
        res = FxmlUtil.validateEmptyIds(inputMapIds);
        if(res != null) {
            FxmlUtil.callErrorAlert(res);
            return;
        }

        var inputDoubleFields = Map.of(sellPriceTextField, "Sell Price");
        res = FxmlUtil.validateDouble(inputDoubleFields);
        if(res != null) {
            FxmlUtil.callErrorAlert(res);
            return;
        }

        productHandler.saveProduct(codeTextField, nameTextField, selectedCategory.getId(), selectedCompany.getId(),
                selectedSupplier.getId(), buyPriceTextField, sellPriceTextField, quantityTextField, isServiceCheckbox.isSelected());
        clearBtn();
        searchBtn();
    }

    public void editBtn() {
        if (productTable.getSelectionModel().getSelectedItem() == null) {
            FxmlUtil.callErrorAlert("Please select product from table to edit.");
            return;
        }

        productHandler.editProduct(selectedProduct.getId(), codeTextField, nameTextField, selectedCategory.getId(),
                selectedCompany.getId(), selectedSupplier.getId(), buyPriceTextField, sellPriceTextField, quantityTextField,
                isServiceCheckbox.isSelected());
        searchBtn();
    }

    public void clearBtn() {
        codeTextField.clear();
        nameTextField.clear();
        categoryTextField.clear();
        this.selectedCategory = new GenericFromDto();
        companyTextField.clear();
        this.selectedCompany = new GenericFromDto();
        supplierTextField.clear();
        this.selectedSupplier = new GenericFromDto();
        buyPriceTextField.clear();
        sellPriceTextField.clear();
        quantityTextField.clear();

        // search fields
        codeSearchTextField.clear();
        nameSearchTextField.clear();
        categorySearchTextField.clear();
        this.selectedSearchCategory = new GenericFromDto();
        companySearchTextField.clear();
        this.selectedSearchCompany = new GenericFromDto();
        supplierSearchTextField.clear();
        this.selectedSearchSupplier = new GenericFromDto();
        buyPriceSearchTextField.clear();
        sellPriceSearchTextField.clear();
        quantitySearchTextField.clear();
        isServiceCheckbox.setSelected(false);

        productTable.getItems().clear();
    }

    public void deleteBtn() {
        if (productTable.getSelectionModel().getSelectedItem() == null) {
            FxmlUtil.callErrorAlert("Please select product from table to delete.");
            return;
        }

        productHandler.deleteProduct(selectedProduct.getId());
        searchBtn();
    }

    public void browseBtn() throws IOException {
        if (productTable.getSelectionModel().getSelectedItem() == null) {
            FxmlUtil.callErrorAlert("Please select product from table to upload a picture.");
            return;
        }

        File selectedFile = fileChooser.showOpenDialog(nameTextField.getScene().getWindow());
        if (selectedFile != null) {
            byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
            productHandler.saveProductPhoto(selectedProduct.getId(), fileBytes);
            FxmlUtil.callInformationAlert("Successfully saved picture for product");
           Image image = new Image(new ByteArrayInputStream(fileBytes));
           productImage.setImage(image);
           searchBtn();
        }

    }

    public void searchBtn() {
        if(StringUtils.isEmpty(companySearchTextField.getText())){
            selectedSearchCompany.setId(null);
        }
        if(StringUtils.isEmpty(supplierSearchTextField.getText())){
            selectedSearchSupplier.setId(null);
        }
        if(StringUtils.isEmpty(categorySearchTextField.getText())){
            selectedSearchCategory.setId(null);
        }

        productHandler.searchProduct(codeSearchTextField, nameSearchTextField,
                selectedSearchCategory.getId(), selectedSearchCompany.getId(), selectedSearchSupplier.getId(),
                buyPriceSearchTextField, sellPriceSearchTextField, quantitySearchTextField, productTable);
    }

    public void categoryPopup() {
        PopupController controller = applicationContext.getBean(PopupController.class);
        controller.setEntityName(GenericFormValue.PRODUCT_CATEGORY.getValue());
        controller.setValueToSearch(categoryTextField.getText());
        FxmlUtil.callPopupForm(genericFormHandler, controller, applicationContext);
        if(controller.getSelectedEntity() == null) return;
        categoryTextField.setText(controller.getSelectedEntity().getName());
        this.selectedCategory = controller.getSelectedEntity();
        companyTextField.requestFocus();
    }

    public void supplierPopup() {
        PopupController controller = applicationContext.getBean(PopupController.class);
        controller.setEntityName(GenericFormValue.SUPPLIER.getValue());
        controller.setValueToSearch(supplierTextField.getText());
        FxmlUtil.callPopupForm(genericFormHandler, controller, applicationContext);
        if(controller.getSelectedEntity() == null) return;
        supplierTextField.setText(controller.getSelectedEntity().getName());
        this.selectedSupplier = controller.getSelectedEntity();
        buyPriceTextField.requestFocus();
    }

    public void companyPopup() {
        PopupController controller = applicationContext.getBean(PopupController.class);
        controller.setEntityName(GenericFormValue.PRODUCT_COMPANY.getValue());
        controller.setValueToSearch(companyTextField.getText());
        FxmlUtil.callPopupForm(genericFormHandler, controller, applicationContext);
        if(controller.getSelectedEntity() == null) return;
        companyTextField.setText(controller.getSelectedEntity().getName());
        this.selectedCompany = controller.getSelectedEntity();
        supplierTextField.requestFocus();
    }

    public void categorySearchPopup() {
        PopupController controller = applicationContext.getBean(PopupController.class);
        controller.setEntityName(GenericFormValue.PRODUCT_CATEGORY.getValue());
        controller.setValueToSearch(categorySearchTextField.getText());
        FxmlUtil.callPopupForm(genericFormHandler, controller, applicationContext);
        if(controller.getSelectedEntity() == null) return;
        categorySearchTextField.setText(controller.getSelectedEntity().getName());
        this.selectedSearchCategory = controller.getSelectedEntity();
        companySearchTextField.requestFocus();
        searchBtn();
    }

    public void supplierSearchPopup() {
        PopupController controller = applicationContext.getBean(PopupController.class);
        controller.setEntityName(GenericFormValue.SUPPLIER.getValue());
        controller.setValueToSearch(supplierSearchTextField.getText());
        FxmlUtil.callPopupForm(genericFormHandler, controller, applicationContext);
        if(controller.getSelectedEntity() == null) return;
        supplierSearchTextField.setText(controller.getSelectedEntity().getName());
        this.selectedSearchSupplier = controller.getSelectedEntity();
        searchBtn();
    }

    public void companySearchPopup() {
        PopupController controller = applicationContext.getBean(PopupController.class);
        controller.setEntityName(GenericFormValue.PRODUCT_COMPANY.getValue());
        controller.setValueToSearch(companySearchTextField.getText());
        FxmlUtil.callPopupForm(genericFormHandler, controller, applicationContext);
        if(controller.getSelectedEntity() == null) return;
        companySearchTextField.setText(controller.getSelectedEntity().getName());
        this.selectedSearchCompany = controller.getSelectedEntity();
        buyPriceSearchTextField.requestFocus();
        searchBtn();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        productTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                this.selectedProduct = productTable.getSelectionModel().getSelectedItem();
                if(selectedProduct == null) {
                    return;
                }
                nameTextField.setText(selectedProduct.getName());
                codeTextField.setText(selectedProduct.getCode());
                categoryTextField.setText(selectedProduct.getCategoryName());
                this.selectedCategory = GenericFromDto.builder().id(selectedProduct.getCategoryId()).build();

                supplierTextField.setText(selectedProduct.getSupplierName());
                this.selectedSupplier = GenericFromDto.builder().id(selectedProduct.getSupplierId()).build();

                companyTextField.setText(selectedProduct.getProductCompanyName());
                this.selectedCompany = GenericFromDto.builder().id(selectedProduct.getProductCompanyId()).build();

                buyPriceTextField.setText(selectedProduct.getBuyPrice().toString());
                sellPriceTextField.setText(selectedProduct.getSellPrice().toString());
                quantityTextField.setText(String.valueOf(selectedProduct.getQuantity()));
                isServiceCheckbox.setSelected(selectedProduct.isService());
                if(selectedProduct.getImage() != null) {
                    Image image = new Image(new ByteArrayInputStream(selectedProduct.getImage()));
                    productImage.setImage(image);
                } else {
                    productImage.setImage(null);
                }
            }
        });

        isServiceCheckbox.setOnMouseClicked(event -> {
            if(isServiceCheckbox.isSelected()) {
                String none = "NONE";
                categoryTextField.setText(none);
                supplierTextField.setText(none);
                companyTextField.setText(none);
                buyPriceTextField.setText("0.0");
                quantityTextField.setText("100000");
                selectedCategory = genericFormHandler.getNone(GenericFormValue.PRODUCT_CATEGORY.getValue());
                selectedSupplier = genericFormHandler.getNone(GenericFormValue.SUPPLIER.getValue());
                selectedCompany = genericFormHandler.getNone(GenericFormValue.PRODUCT_COMPANY.getValue());
            } else {
                categoryTextField.clear();
                supplierTextField.clear();
                companyTextField.clear();
                quantityTextField.clear();
                buyPriceTextField.clear();
                selectedCompany = null;
                selectedCategory = null;
                selectedSupplier = null;
            }
        });




        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);
    }
}
