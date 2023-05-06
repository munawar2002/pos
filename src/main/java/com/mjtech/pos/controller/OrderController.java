package com.mjtech.pos.controller;

import com.mjtech.pos.constant.Gender;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.entity.*;
import com.mjtech.pos.repository.ProductRepository;
import com.mjtech.pos.service.CustomerService;
import com.mjtech.pos.util.FxmlUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
public class OrderController implements ControllerInterface, Initializable {

    @FXML
    private TextField orderNoTextField;

    @FXML
    private TextField prevOrderTextField;

    @FXML
    private TextField customerTextField;

    @FXML
    private TextField productTextField;

    @FXML
    private TextField quantityTextField;

    @FXML
    private TableView<OrderTableDto> orderTable;

    @FXML
    private TextField invoiceNoSearchTextField;

    @FXML
    private TextField discountAmountTextField;

    @FXML
    private TextField discountPercentTextField;

    @FXML
    private TextField gstTextField;

    @FXML
    private TextField totalAmountTextField;

    @FXML
    private TableView<Invoice> pendingInvoiceTable;

    @FXML
    private TableView<InvoiceDetail> invoiceTable;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductRepository productRepository;

    private Customer selectedCustomer;
    private ProductDto selectedProduct;

    @FXML
    public void saveOrderBtn() {

    }

    @FXML
    public void deleteItemBtn() {

    }

    @FXML
    public void orderClearBtn() {

    }

    @FXML
    public void readyToPayBtn() {

    }

    @FXML
    public void generateInvoiceBtn() {

    }

    @FXML
    public void invoiceClearBtn() {

    }

    @FXML
    public void editOrderBtn() {

    }

    @FXML
    public void invoiceSearchBtn() {

    }

    @FXML
    public void customerPopup() {
        CustomerPopupController controller = applicationContext.getBean(CustomerPopupController.class);
        FxmlUtil.callPopupForm("/fxml/customerPopup.fxml", controller, applicationContext);
        if(controller.getSelectedCustomer() == null) return;
        customerTextField.setText(controller.getSelectedCustomer().getFullName());
        this.selectedCustomer = controller.getSelectedCustomer();
        productTextField.requestFocus();
    }

    @FXML
    public void productPopup() {
        ProductPopupController controller = applicationContext.getBean(ProductPopupController.class);
        controller.setOrderTableDtoList(orderTable.getItems());
        FxmlUtil.callPopupForm("/fxml/productPopup.fxml", controller, applicationContext);
        if(controller.getSelectedProduct() == null) return;
        productTextField.setText(controller.getSelectedProduct().getCode() + " - " + controller.getSelectedProduct().getName());
        this.selectedProduct = controller.getSelectedProduct();
        quantityTextField.requestFocus();
    }

    public void addItemInOrderTable() {
        Product product = productRepository.findById(selectedProduct.getId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Product not found with id %d", selectedProduct.getId())));
        int quantity = Integer.parseInt(quantityTextField.getText());
        var orderTableDto = OrderTableDto.builder()
                .code(product.getCode())
                .productName(product.getName())
                .productId(product.getId())
                .quantity(quantity)
                .price(product.getSellPrice())
                .total(product.getSellPrice() * quantity)
                .build();
        orderTable.getItems().add(orderTableDto);
        quantityTextField.clear();
        productTextField.clear();
        productTextField.requestFocus();
        this.selectedProduct = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Customer> customers = customerService.searchCustomer("Mr General", Gender.MALE.name(),
                "", "");
        if(!customers.isEmpty()) {
            this.selectedCustomer = customers.get(0);
        }
        customerTextField.setText(selectedCustomer.getFullName());
        Platform.runLater(() -> productTextField.requestFocus());

        quantityTextField.setOnAction(event -> {
            addItemInOrderTable();
        });

        setupOrderTable();
    }

    private void setupOrderTable() {
        var columnMap = Map.of(
                "Code", "code",
                "Product Name", "productName",
                "Quantity", "quantity",
                "Price", "price",
                "Total", "total");

        for (Map.Entry<String,String> entry : columnMap.entrySet()) {
            orderTable.getColumns().forEach(col -> {
                if(col.getText().equalsIgnoreCase(entry.getKey())) {
                    col.setCellValueFactory(new PropertyValueFactory<>(entry.getValue()));
                }
            });
        }
    }

}
