package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.RefundAndExchangeHandler;
import com.mjtech.pos.constant.Formats;
import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.dto.InvoiceTableDto;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.entity.Invoice;
import com.mjtech.pos.repository.InvoiceRepository;
import com.mjtech.pos.util.FxmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;


@Controller
@Slf4j
public class RefundController implements ControllerInterface, Initializable {

    private Invoice refundInvoice;

    @FXML
    private TextField orderNoTextField;

    @FXML
    private TableView<InvoiceTableDto> invoiceTable;

    @FXML
    private TextField addProductTextField, addQuantityTextField, amountTextField, gstTextField, discountAmountTextField,
            discountPercentTextField, totalDiscountTextField, totalAmountTextField, cashReceivedTextField,
            balanceTextField, remarksTextField;

    @FXML
    private RadioButton refundOnlyRadioBtn, refundAndExchangeRadioBtn, refundWithoutInvoiceRadioBtn;

    @FXML
    private CheckBox selectAllCheckbox;

    @FXML
    private ToggleGroup refundExchangeGroup;

    @FXML
    private TableView<OrderTableDto> refundTable;

    @FXML
    private TableView<OrderTableDto> invoiceDetailTable;

    @FXML
    private TableColumn<OrderTableDto, Boolean> checkBoxColumn;

    @FXML
    private Label totalAmountLabel;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private RefundAndExchangeHandler refundAndExchangeHandler;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Value("${tax.percentage}")
    private Double taxPercentage;

    private InvoiceTableDto selectedInvoiceFromPopup;
    private Invoice selectedInvoice;

    public void invoiceClearBtn() {
            invoiceDetailTable.getItems().clear();
            orderNoTextField.clear();
            this.selectedInvoice = null;
            this.selectedInvoiceFromPopup = null;
            selectAllCheckbox.setSelected(false);
    }

    public void readyToRefundBtn() {
        refundInvoice = refundAndExchangeHandler.saveRefundOrder(invoiceDetailTable, selectedInvoice);
        refundAndExchangeHandler.populateRefundTable(refundTable, refundInvoice);
        cashReceivedTextField.setText("0");
        populateAmountFields();
    }

    private void populateAmountFields() {

        double totalInvoiceAmount = refundTable.getItems().stream().mapToDouble(t -> Double.parseDouble(t.getTotal())).sum();
        amountTextField.setText(Formats.getDecimalFormat().format(totalInvoiceAmount));

        double percentageAmount = (totalInvoiceAmount * taxPercentage) / 100;
        gstTextField.setText(Formats.getDecimalFormat().format(percentageAmount));

        double totalAmount = totalInvoiceAmount + percentageAmount;
        totalAmountTextField.setText(Formats.getDecimalFormat().format(-totalAmount));

        calculateBalanceAmount();
    }

    public void refundProductPopup() {

    }

    public void addItemInOrderTable() {

    }

    public void refundWithoutInvoiceAction() {

    }

    public void generateRefundBtn() {
        if(refundOnlyRadioBtn.isSelected() && (refundInvoice == null || refundTable.getItems().isEmpty())) {
           FxmlUtil.callErrorAlert("Please select invoice to refund the items.");
           return;
        }

//        double cashReceived = cashReceivedTextField.getText().isEmpty() ? 0 : Double.parseDouble(cashReceivedTextField.getText());
//        double totalAmount = Double.parseDouble(totalAmountTextField.getText());
//        calculateBalanceAmount();
//        double balanceAmount = Double.parseDouble(balanceTextField.getText());
//        double discountAmount = discountAmountTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(discountAmountTextField.getText());
//        double discountPercent = discountPercentTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(discountPercentTextField.getText());
//        double discountTotal = totalDiscountTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(totalDiscountTextField.getText());
//        String remarks = remarksTextField.getText();
//        double amount = Double.parseDouble(amountTextField.getText());
//        double gst = Double.parseDouble(gstTextField.getText());


//        var invoiceDto = InvoiceDto.builder()
//                .invoiceId(this.refundInvoice.getId())
//                .cashReceived(cashReceived)
//                .totalAmount(totalAmount)
//                .balanceAmount(balanceAmount)
//                .discountAmount(discountAmount)
//                .discountPercent(discountPercent)
//                .discountTotal(discountTotal)
//                .remarks(remarks)
//                .amount(amount)
//                .paymentType(paymentType)
//                .gst(gst)
//                .build();

        refundAndExchangeHandler.generateRefundInvoice(refundInvoice, refundTable);
    }

    public void orderClearBtn() {

    }

    public void generateExchangeBtn() {

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeSelectColumnCheckboxAndEditable();
        disableAndClearDiscountTextFields();
        makeTextFieldsToReadNumbersOnly();

        // selectAll functionality
        selectAllCheckbox.setOnAction(event -> {
            if(selectAllCheckbox.isSelected()) {
                invoiceDetailTable.getItems().forEach(item -> item.setSelected(true));
            } else {
                invoiceDetailTable.getItems().forEach(item -> item.setSelected(false));
            }
        });


        refundOnlyRadioBtn.setOnAction(event -> {
            if(refundOnlyRadioBtn.isSelected()) {
                disableAndClearDiscountTextFields();
            }
        });

        refundWithoutInvoiceRadioBtn.setOnAction(event -> {
            if(refundWithoutInvoiceRadioBtn.isSelected()) {
                disableAndClearDiscountTextFields();
            }
        });

        refundAndExchangeRadioBtn.setOnAction(event -> {
            if(refundAndExchangeRadioBtn.isSelected()) {
                enableDiscountTextFields();
            }
        });

        totalAmountTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                double totalRefundAmount = totalAmountTextField.getText().isEmpty() ? 0 : Double.parseDouble(totalAmountTextField.getText());
                double gst = gstTextField.getText().isEmpty() ? 0 : Double.parseDouble(gstTextField.getText());
                double amount = amountTextField.getText().isEmpty() ? 0 : Double.parseDouble(amountTextField.getText());

                if (totalRefundAmount > (amount + gst)) {
                    FxmlUtil.callErrorAlert("Total Refund Amount can't be greater than total order cost. Please write number between 0 and " + (amount + gst));
                    totalAmountTextField.clear();
                    totalAmountTextField.requestFocus();
                } else {
                    calculateBalanceAmount();
                }
            }
        });

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

    private void disableAndClearDiscountTextFields() {
        discountPercentTextField.setEditable(false);
        discountAmountTextField.setEditable(false);
        totalDiscountTextField.setEditable(false);
        totalAmountTextField.setEditable(true);
        discountPercentTextField.clear();
        discountAmountTextField.clear();
        totalDiscountTextField.clear();
    }

    private void enableDiscountTextFields() {
        discountPercentTextField.setEditable(true);
        discountAmountTextField.setEditable(true);
    }

    private void makeSelectColumnCheckboxAndEditable() {
        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        checkBoxColumn.setCellFactory(column -> {
            CheckBoxTableCell<OrderTableDto, Boolean> cell = new CheckBoxTableCell<>();
            cell.setEditable(true);
            cell.setOnMouseClicked(event -> {
                if (!cell.isEditing() && cell.getTableRow() != null) {
                    cell.getTableView().edit(cell.getTableRow().getIndex(), checkBoxColumn);
                    cell.commitEdit(!cell.getItem());
                }
            });
            return cell;
        });

        // Make the checkbox column editable
        checkBoxColumn.setEditable(true);

        // Enable editing for the entire table
        invoiceDetailTable.setEditable(true);
    }


    @FXML
    public void invoicePopup() {
        InvoicePopupController controller = applicationContext.getBean(InvoicePopupController.class);
        controller.setOrderNoSearch(orderNoTextField.getText());
        controller.setInvoiceStatusSearch(InvoiceStatus.PAID);
        controller.setInvoiceStatusTextFieldEditable(false);
        FxmlUtil.callPopupForm("/fxml/invoicePopup.fxml", controller, applicationContext);
        if(controller.getSelectedInvoice() == null) return;
        orderNoTextField.setText(controller.getSelectedInvoice().getOrderNo());
        this.selectedInvoiceFromPopup = controller.getSelectedInvoice();
        selectedInvoice = invoiceRepository.findById(selectedInvoiceFromPopup.getInvoiceId())
                        .orElseThrow(() -> new RuntimeException("Invoice not found with id "+ selectedInvoiceFromPopup.getInvoiceId()));
        refundAndExchangeHandler.populateInvoiceDetailTable(invoiceDetailTable, selectedInvoice);
        invoiceDetailTable.requestFocus();
    }



//    private void setInvoiceFields() {
//        InvoiceTableDto selectedItem = invoiceTable.getSelectionModel().getSelectedItem();
//        if(selectedItem != null) {
//            searchRemarksTextField.setText(selectedItem.getRemarks());
//            refundAndExchangeHandler.populateInvoiceItemTable(orderTable, selectedItem.getInvoiceId());
//            if(refundOnlyRadioBtn.isSelected()) {
//                Double total = orderTable.getItems().stream().mapToDouble(dto -> Double.parseDouble(dto.getTotal())).sum();
//                double gst = (total * taxPercentage) / 100;
//                Invoice invoice = invoiceRepository.findById(selectedInvoice.getInvoiceId())
//                        .orElseThrow(() -> new RuntimeException("Invoice not found with id " + selectedItem.getInvoiceId()));
//
//                amountTextField.setText(Formats.getDecimalFormat().format(total));
//                gstTextField.setText(Formats.getDecimalFormat().format(gst));
//                discountAmountTextField.setText(Formats.getDecimalFormat().format(invoice.getDiscountAmount()));
//                discountPercentTextField.setText(Formats.getDecimalFormat().format(invoice.getDiscountPercentage()));
//                calculateAndSetTotalDiscount();
//                calculateTotalAmount();
//            }
//        }
    //}

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
    }

    private void calculateTotalAmount() {
        double amount = Double.parseDouble(amountTextField.getText());
        double gst = Double.parseDouble(gstTextField.getText());
        double totalDiscount = totalDiscountTextField.getText().isEmpty() ? 0 :
                Double.parseDouble(totalDiscountTextField.getText());
        double totalAmount = amount + gst - totalDiscount;
        totalAmountTextField.setText(Formats.getDecimalFormat().format(-totalAmount));
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
        addQuantityTextField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
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
}
