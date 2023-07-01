package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.OrderAndInvoiceHandler;
import com.mjtech.pos.constant.Formats;
import com.mjtech.pos.constant.Gender;
import com.mjtech.pos.constant.PaymentType;
import com.mjtech.pos.dto.InvoiceDto;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.dto.InvoiceTableDto;
import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.entity.*;
import com.mjtech.pos.repository.ProductRepository;
import com.mjtech.pos.service.CustomerService;
import com.mjtech.pos.util.FxmlUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
@Slf4j
public class OrderController implements ControllerInterface, Initializable {

    @FXML
    private TextField customerTextField;

    @FXML
    private TextField productTextField;

    @FXML
    private TextField quantityTextField;

    @FXML
    private TextField totalOrderTextField;

    @FXML
    private TableView<OrderTableDto> orderTable;

    @FXML
    private TextField orderNoSearchTextField;

    @FXML
    private TextField discountAmountTextField, discountPercentTextField, totalDiscountTextField;

    @FXML
    private TextField gstTextField;

    @FXML
    private TextField amountTextField, cashReceivedTextField, balanceTextField, totalAmountTextField;

    @FXML
    private RadioButton cashRadioBtn, cardRadioBtn, cashAndCardRadioBtn;

    @FXML
    private TextField remarksTextField, orderRemarksTextField;

    @FXML
    private TableView<InvoiceTableDto> pendingInvoiceTable;

    @FXML
    private TableView<OrderTableDto> invoiceTable;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderAndInvoiceHandler orderAndInvoiceHandler;

    private Customer selectedCustomer;
    private ProductDto selectedProduct;
    private Integer selectedInvoiceId;

    @FXML
    public void saveOrderBtn() {
        orderAndInvoiceHandler.saveOrder(this.selectedCustomer, orderTable.getItems(), pendingInvoiceTable,
                invoiceTable, amountTextField, gstTextField, orderRemarksTextField, remarksTextField);
        clearOrder();
        pendingInvoiceTable.getItems().clear();
        invoiceTable.getItems().clear();
        amountTextField.clear();
        gstTextField.clear();
        orderRemarksTextField.clear();

    }

    private void clearOrder() {
        productTextField.clear();
        quantityTextField.clear();
        orderTable.getItems().clear();
        totalOrderTextField.clear();
        orderRemarksTextField.clear();
        setGeneralCustomer();
    }

    @FXML
    public void deleteItemBtn() {
        OrderTableDto selectedItem = orderTable.getSelectionModel().getSelectedItem();
        if(selectedItem == null) {
            FxmlUtil.callWarningAlert("Please select item from order table to delete");
            return;
        }
        orderTable.getItems().remove(selectedItem);
        orderTable.refresh();
        calculateOrderTotal();
    }

    @FXML
    public void orderClearBtn() {
        clearOrder();

    }

    @FXML
    public void readyToPayBtn() {
        Invoice invoice = orderAndInvoiceHandler.saveOrder(this.selectedCustomer, orderTable.getItems(), pendingInvoiceTable,
                invoiceTable, amountTextField, gstTextField, orderRemarksTextField, remarksTextField);
        this.selectedInvoiceId = invoice.getId();
        calculateAndSetTotalDiscount();
        calculateTotalAmount();
        calculateBalanceAmount();
        clearOrder();
    }

    @FXML
    public void generateInvoiceBtn() {
        PaymentType paymentType = null;
        if((cashRadioBtn.isSelected() || cashAndCardRadioBtn.isSelected()) && cashReceivedTextField.getText().isEmpty()) {
            FxmlUtil.callErrorAlert("Please enter cash received amount");
            cashReceivedTextField.requestFocus();
            return;
        }

        if(cashRadioBtn.isSelected()) {
            double cashReceived = Double.parseDouble(cashReceivedTextField.getText());
            double totalAmount = Double.parseDouble(totalAmountTextField.getText());
            if(cashReceived < totalAmount) {
                double remaining = totalAmount - cashReceived;
                FxmlUtil.callErrorAlert(String.format("Customer have paid less money. Please ask customer to pay more %s",
                        Formats.getDecimalFormat().format(remaining)));
                return;
            }
            paymentType = PaymentType.CASH;
        }

        if(invoiceTable.getItems().isEmpty() || this.selectedInvoiceId == null) {
            FxmlUtil.callErrorAlert("Please create new order or select existing one");
            return;
        }

        if(cardRadioBtn.isSelected()) {
            boolean result = FxmlUtil.callConfirmationAlert(String.format("Have customer paid %s amount by card?", balanceTextField.getText()));
            if(!result) {
                log.error("Ask customer to pay by card!!");
                return;
            }
            paymentType = PaymentType.CARD;
        }

        if(cashAndCardRadioBtn.isSelected()) {
            if(cashReceivedTextField.getText().isEmpty()) {
                FxmlUtil.callErrorAlert("Please enter cash received amount from customer");
                return;
            }
            double cashReceived = Double.parseDouble(cashReceivedTextField.getText());
            double totalAmount = Double.parseDouble(totalAmountTextField.getText());
            double cardRemainingAmount = totalAmount - cashReceived;
            boolean result = FxmlUtil.callConfirmationAlert(String.format("Have customer paid %s amount by card?",
                    Formats.getDecimalFormat().format(cardRemainingAmount)));
            if(!result) {
                log.error(String.format("Ask customer to pay %s by card!!", cardRemainingAmount));
                return;
            }
            paymentType = PaymentType.CASH_AND_CARD;
        }

        double cashReceived = cashReceivedTextField.getText().isEmpty() ? 0 : Double.parseDouble(cashReceivedTextField.getText());
        double totalAmount = Double.parseDouble(totalAmountTextField.getText());
        calculateBalanceAmount();
        double balanceAmount = Double.parseDouble(balanceTextField.getText());
        double discountAmount = discountAmountTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(discountAmountTextField.getText());
        double discountPercent = discountPercentTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(discountPercentTextField.getText());
        double discountTotal = totalDiscountTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(totalDiscountTextField.getText());
        String remarks = remarksTextField.getText();
        double amount = Double.parseDouble(amountTextField.getText());
        double gst = Double.parseDouble(gstTextField.getText());

        var invoiceDto = InvoiceDto.builder()
                .invoiceId(this.selectedInvoiceId)
                .cashReceived(cashReceived)
                .totalAmount(totalAmount)
                .balanceAmount(balanceAmount)
                .discountAmount(discountAmount)
                .discountPercent(discountPercent)
                .discountTotal(discountTotal)
                .remarks(remarks)
                .amount(amount)
                .paymentType(paymentType)
                .gst(gst)
                .build();

        orderAndInvoiceHandler.generateInvoice(invoiceDto);
        FxmlUtil.callInformationAlert("Invoice Successfully Created!!!");
        invoiceClearBtn();
        productTextField.requestFocus();
        cashRadioBtn.setSelected(true);
    }

    @FXML
    public void invoiceClearBtn() {
        invoiceTable.getItems().clear();
        pendingInvoiceTable.getItems().clear();
        orderNoSearchTextField.clear();
        amountTextField.clear();
        gstTextField.clear();
        discountAmountTextField.clear();
        discountPercentTextField.clear();
        totalDiscountTextField.clear();
        totalAmountTextField.clear();
        cashReceivedTextField.clear();
        balanceTextField.clear();
        remarksTextField.clear();

    }

    @FXML
    public void editOrderBtn() {
        InvoiceTableDto selectedItem = pendingInvoiceTable.getSelectionModel().getSelectedItem();
        if(selectedItem == null) {
            FxmlUtil.callErrorAlert("Please click order in pending invoice table to edit!");
            return;
        }

        orderAndInvoiceHandler.populateOrderTableByOrderNo(selectedItem.getOrderNo(), orderTable, orderRemarksTextField);
        calculateOrderTotal();
        invoiceClearBtn();
    }

    @FXML
    public void invoiceSearchBtn() {
        orderAndInvoiceHandler.populatePendingInvoiceTable(orderNoSearchTextField, pendingInvoiceTable);
    }

    @FXML
    public void invoiceSearchThroughOrderNo() {
        orderAndInvoiceHandler.populatePendingInvoiceTable(orderNoSearchTextField, pendingInvoiceTable);
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
        if(quantityTextField.getText().isEmpty()) {
            return;
        }
        Product product = productRepository.findById(selectedProduct.getId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Product not found with id %d", selectedProduct.getId())));
        int quantity = Integer.parseInt(quantityTextField.getText());
        var orderTableDto = OrderTableDto.builder()
                .code(product.getCode())
                .productName(product.getName())
                .productId(product.getId())
                .quantity(quantity)
                .price(Formats.getDecimalFormat().format(product.getSellPrice()))
                .total(Formats.getDecimalFormat().format(product.getSellPrice() * quantity))
                .build();
        orderTable.getItems().add(orderTableDto);
        quantityTextField.clear();
        productTextField.clear();
        productTextField.requestFocus();
        this.selectedProduct = null;
        calculateOrderTotal();
    }

    private void calculateOrderTotal() {
        double totalOrderSum = orderTable.getItems().stream().mapToDouble(t -> Double.parseDouble(t.getTotal())).sum();
        totalOrderTextField.setText(String.valueOf(Formats.getDecimalFormat().format(totalOrderSum)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setGeneralCustomer();

        quantityTextField.setOnAction(event -> {
            addItemInOrderTable();
        });

        setupOrderTable();

        pendingInvoiceTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                InvoiceTableDto selectedItem = pendingInvoiceTable.getSelectionModel().getSelectedItem();
                if(selectedItem == null) return;
                orderAndInvoiceHandler.populateInvoiceTable(invoiceTable, selectedItem.getInvoiceId(),
                        amountTextField, gstTextField, remarksTextField);
                calculateAndSetTotalDiscount();
                calculateTotalAmount();
                calculateBalanceAmount();
                this.selectedInvoiceId = selectedItem.getInvoiceId();
            }
        });
        cashRadioBtn.setSelected(true);
        makeTextFieldsToReadNumbersOnly();
        discountEvents();

        cashReceivedTextField.setOnAction(event -> {
            calculateBalanceAmount();
        });
        cashReceivedTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // Code to execute when focus is lost
                calculateBalanceAmount();
            }
        });

    }

    private void calculateBalanceAmount() {
        String cashReceivedText = cashReceivedTextField.getText();
        double totalAmount = Double.parseDouble(totalAmountTextField.getText());
        double cashReceived = 0;
        if(!cashReceivedText.isEmpty()) {
            cashReceived = Double.parseDouble(cashReceivedText);
        }
        double balanceAmount = totalAmount - cashReceived;
        balanceTextField.setText(Formats.getDecimalFormat().format(balanceAmount));
    }

    private void calculateTotalAmount() {
        double amount = Double.parseDouble(amountTextField.getText());
        double gst = Double.parseDouble(gstTextField.getText());
        double totalDiscount = totalDiscountTextField.getText().isEmpty() ? 0 :
                Double.parseDouble(totalDiscountTextField.getText());
        double totalAmount = amount + gst - totalDiscount;
        totalAmountTextField.setText(Formats.getDecimalFormat().format(totalAmount));
    }

    private void discountEvents() {
        discountAmountTextField.setOnAction(event -> {
            calculateAndSetTotalDiscount();
        });
        discountAmountTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // Code to execute when focus is lost
                calculateAndSetTotalDiscount();
            }
        });
        discountPercentTextField.setOnAction(event -> {
            calculateAndSetTotalDiscount();
        });
        discountPercentTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // Code to execute when focus is lost
                calculateAndSetTotalDiscount();
            }
        });
    }

    private void calculateAndSetTotalDiscount() {
        String discountAmount = discountAmountTextField.getText();
        String discountPercentage = discountPercentTextField.getText();

        double totalDiscount = 0.0;
        if(!discountAmount.isEmpty()) {
            totalDiscount += Double.parseDouble(discountAmount);
        }

        if(!discountPercentage.isEmpty()) {
            double total = Double.parseDouble(amountTextField.getText());
            double disAmount = (total * Double.parseDouble(discountPercentage)) / 100;
            totalDiscount += disAmount;
        }
        totalDiscountTextField.setText(Formats.getDecimalFormat().format(totalDiscount));
        calculateTotalAmount();
        calculateBalanceAmount();
    }

    private void setGeneralCustomer() {
        List<Customer> customers = customerService.searchCustomer("Mr General", Gender.MALE.name(),
                "", "");
        if(!customers.isEmpty()) {
            this.selectedCustomer = customers.get(0);
        }
        customerTextField.setText(selectedCustomer.getFullName());
        Platform.runLater(() -> productTextField.requestFocus());
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

    private void makeTextFieldsToReadNumbersOnly() {
        // cashReceivedTextField to take input only number with precision.
        cashReceivedTextField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();
            if (!input.matches("[\\d.]")) {
                event.consume();
            }
        });
        // quantityTextField to take input only number.
        quantityTextField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();
            if (!input.matches("\\d")) {
                event.consume();
            }
        });
        // discountAmountTextField to take input only number with precision.
        discountAmountTextField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();
            if (!input.matches("[\\d.]")) {
                event.consume();
            }
        });
        // discountPercentTextField to take input only number with precision.
        discountPercentTextField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();
            if (!input.matches("[\\d.]")) {
                event.consume();
            }
        });

        // discountPercentTextField to take input only number with precision.
        cashReceivedTextField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();
            if (!input.matches("[\\d.]")) {
                event.consume();
            }
        });
    }

}
