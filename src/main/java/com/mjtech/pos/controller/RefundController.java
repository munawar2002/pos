package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.RefundAndExchangeHandler;
import com.mjtech.pos.dto.InvoiceDto;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.entity.Customer;
import com.mjtech.pos.util.FxmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;


@Controller
@Slf4j
public class RefundController implements ControllerInterface, Initializable {

    @FXML
    private TextField customerTextField, productTextField, orderNoTextField, invoiceNoTextField, fromAmountTextField,
            toAmountTextField, cashReceivedSearchTextField, cardReceivedSearchTextField, searchRemarksTextField;

    @FXML
    private DatePicker fromDatePicker, toDatePicker;

    @FXML
    private TableView<InvoiceDto> invoiceTable;

    @FXML
    private TextField addProductTextField, addQuantityTextField, amountTextField, gstTextField, discountAmountTextField,
            discountPercentTextField, totalDiscountTextField, totalAmountTextField, cashReceivedTextField,
            balanceTextField, remarksTextField;

    @FXML
    private TableView<OrderTableDto> orderTable;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private RefundAndExchangeHandler refundAndExchangeHandler;

    private InvoiceDto selectedInvoice;
    private Customer selectedCustomer;
    private ProductDto selectedProduct;
    private Integer selectedInvoiceId;

    public void invoiceClearBtn() {
        customerTextField.clear();
        productTextField.clear();
        orderNoTextField.clear();
        invoiceNoTextField.clear();
        fromAmountTextField.clear();
        toAmountTextField.clear();
        cashReceivedSearchTextField.clear();
        cardReceivedSearchTextField.clear();
        selectedProduct = null;
        selectedCustomer = null;
        invoiceTable.getItems().clear();
        searchRemarksTextField.clear();
        selectedInvoice = null;
        invoiceTable.refresh();
    }

    public void readyToRefundBtn() {

    }

    public void customerPopup() {
        CustomerPopupController controller = applicationContext.getBean(CustomerPopupController.class);
        FxmlUtil.callPopupForm("/fxml/customerPopup.fxml", controller, applicationContext);
        if(controller.getSelectedCustomer() == null) return;
        customerTextField.setText(controller.getSelectedCustomer().getFullName());
        this.selectedCustomer = controller.getSelectedCustomer();
        productTextField.requestFocus();
    }

    public void productPopup() {
        ProductPopupController controller = applicationContext.getBean(ProductPopupController.class);
        controller.setOrderTableDtoList(orderTable.getItems());
        FxmlUtil.callPopupForm("/fxml/productPopup.fxml", controller, applicationContext);
        if(controller.getSelectedProduct() == null) return;
        productTextField.setText(controller.getSelectedProduct().getCode() + " - " + controller.getSelectedProduct().getName());
        this.selectedProduct = controller.getSelectedProduct();
        orderNoTextField.requestFocus();
    }

    public void searchInvoices() {
        if(StringUtils.isEmpty(customerTextField.getText())) {
            selectedCustomer = null;
        }

        if(StringUtils.isEmpty(productTextField.getText())) {
            selectedProduct = null;
        }

        refundAndExchangeHandler.populateInvoiceTable(selectedCustomer, selectedProduct, fromDatePicker, toDatePicker,
                invoiceNoTextField, orderNoTextField, fromAmountTextField, toAmountTextField,
                cashReceivedSearchTextField, cardReceivedSearchTextField, invoiceTable);
    }

    public void refundProductPopup() {

    }

    public void addItemInOrderTable() {

    }

    public void refundWithoutInvoiceAction() {

    }

    public void generateRefundBtn() {

    }

    public void orderClearBtn() {

    }

    public void generateExchangeBtn() {

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
