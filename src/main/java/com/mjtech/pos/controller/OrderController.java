package com.mjtech.pos.controller;

import com.mjtech.pos.entity.Invoice;
import com.mjtech.pos.entity.InvoiceDetail;
import com.mjtech.pos.entity.Order;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController {

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
    private TableView<Order> orderTable;

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

}
