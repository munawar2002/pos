package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.GenericFormHandler;
import com.mjtech.pos.GuiHandler.ProductHandler;
import com.mjtech.pos.constant.GenericFormValue;
import com.mjtech.pos.dto.GenericFromDto;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.util.FxmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
@Scope("prototype")
public class ProductPopupController implements ControllerInterface, Initializable {

    @Autowired
    private ProductHandler productHandler;

    @Autowired
    private GenericFormHandler genericFormHandler;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

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

    private ProductDto selectedProduct;

    private List<OrderTableDto> orderTableDtoList;

    public ProductDto getSelectedProduct() {
        return selectedProduct;
    }

    public void setOrderTableDtoList(List<OrderTableDto> orderTableDtoList) {
        this.orderTableDtoList = orderTableDtoList;
    }

    @FXML
    private TableView<ProductDto> productTable;

    private GenericFromDto selectedSearchCategory = new GenericFromDto();

    private GenericFromDto selectedSearchSupplier = new GenericFromDto();

    private GenericFromDto selectedSearchCompany = new GenericFromDto();

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

        List<Integer> productIds = orderTableDtoList.stream().map(OrderTableDto::getProductId).toList();

        productHandler.searchProduct(codeSearchTextField, nameSearchTextField,
                selectedSearchCategory.getId(), selectedSearchCompany.getId(), selectedSearchSupplier.getId(),
                buyPriceSearchTextField, sellPriceSearchTextField, quantitySearchTextField, productTable, productIds);
        productTable.requestFocus();
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
        searchBtn();
        codeSearchTextField.setOnAction(event -> {
            searchBtn();
        });
        nameSearchTextField.setOnAction(event -> {
            searchBtn();
        });
        buyPriceSearchTextField.setOnAction(event -> {
            searchBtn();
        });
        sellPriceSearchTextField.setOnAction(event -> {
            searchBtn();
        });
        quantitySearchTextField.setOnAction(event -> {
            searchBtn();
        });

        productTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click detected
                this.selectedProduct = productTable.getSelectionModel().getSelectedItem();
                Stage stage = (Stage) codeSearchTextField.getScene().getWindow();
                stage.close();
            }
        });

        productTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) { // Double-click detected
                this.selectedProduct = productTable.getSelectionModel().getSelectedItem();
                Stage stage = (Stage) codeSearchTextField.getScene().getWindow();
                stage.close();
            }
        });
    }
}
