package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.RefundAndExchangeHandler;
import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.dto.InvoiceDto;
import com.mjtech.pos.dto.InvoiceTableDto;
import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.entity.Customer;
import com.mjtech.pos.util.FxmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
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
import java.util.ResourceBundle;

@Controller
@Scope("prototype")
public class InvoicePopupController implements ControllerInterface, Initializable {

    @FXML
    private TextField customerTextField, productTextField, orderNoTextField, invoiceNoTextField, fromAmountTextField,
            toAmountTextField, cashReceivedSearchTextField, cardReceivedSearchTextField, statusTextField;

    @FXML
    private DatePicker fromDatePicker, toDatePicker;

    @FXML
    private TableView<InvoiceTableDto> invoiceTable;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private RefundAndExchangeHandler refundAndExchangeHandler;

    private InvoiceTableDto selectedInvoice;
    private Customer selectedCustomer;
    private ProductDto selectedProduct;

    private String orderNoSearch;
    private InvoiceStatus invoiceStatusSearch;
    private boolean isInvoiceStatusTextFieldEditable = true;


    public String getOrderNoSearch() {
        return orderNoSearch;
    }

    public void setOrderNoSearch(String orderNoSearch) {
        this.orderNoSearch = orderNoSearch;
    }

    public InvoiceTableDto getSelectedInvoice() {
        return selectedInvoice;
    }

    public void setSelectedInvoice(InvoiceTableDto selectedInvoice) {
        this.selectedInvoice = selectedInvoice;
    }

    public InvoiceStatus getInvoiceStatusSearch() {
        return invoiceStatusSearch;
    }

    public boolean isInvoiceStatusTextFieldEditable() {
        return isInvoiceStatusTextFieldEditable;
    }

    public void setInvoiceStatusTextFieldEditable(boolean invoiceStatusTextFieldEditable) {
        isInvoiceStatusTextFieldEditable = invoiceStatusTextFieldEditable;
    }

    public void setInvoiceStatusSearch(InvoiceStatus invoiceStatusSearch) {
        this.invoiceStatusSearch = invoiceStatusSearch;
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
                cashReceivedSearchTextField, cardReceivedSearchTextField, statusTextField, invoiceTable);
    }

    public void customerPopup() {
        CustomerPopupController controller = applicationContext.getBean(CustomerPopupController.class);
        FxmlUtil.callPopupForm("/fxml/customerPopup.fxml", controller, applicationContext);
        if(controller.getSelectedCustomer() == null) return;
        customerTextField.setText(controller.getSelectedCustomer().getFullName());
        this.selectedCustomer = controller.getSelectedCustomer();
        orderNoTextField.requestFocus();
        searchInvoices();
    }

    public void productPopup() {
        ProductPopupController controller = applicationContext.getBean(ProductPopupController.class);
        FxmlUtil.callPopupForm("/fxml/productPopup.fxml", controller, applicationContext);
        if(controller.getSelectedProduct() == null) return;
        productTextField.setText(controller.getSelectedProduct().getCode() + " - " + controller.getSelectedProduct().getName());
        this.selectedProduct = controller.getSelectedProduct();
        statusTextField.requestFocus();
        searchInvoices();
    }

    public void invoiceStatusPopup() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(!isInvoiceStatusTextFieldEditable) {
            statusTextField.setEditable(false);
        }
        statusTextField.setText(invoiceStatusSearch.name());
        orderNoTextField.setText(orderNoSearch);
        searchInvoices();

        invoiceTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click detected
                this.selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
                Stage stage = (Stage) orderNoTextField.getScene().getWindow();
                stage.close();
            }
        });

        invoiceTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) { // press enter on record.
                this.selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
                Stage stage = (Stage) orderNoTextField.getScene().getWindow();
                stage.close();
            }
        });
    }
}
