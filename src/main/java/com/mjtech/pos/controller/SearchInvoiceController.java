package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.RefundAndExchangeHandler;
import com.mjtech.pos.constant.Formats;
import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.dto.InvoiceTableDto;
import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.entity.Customer;
import com.mjtech.pos.repository.InvoiceRepository;
import com.mjtech.pos.util.FxmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ResourceBundle;

@Controller
public class SearchInvoiceController implements ControllerInterface, Initializable {

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

    @FXML
    private Label currentDaySale, lastDaySale, currentMonthSale, lastMonthSale, cashReceivedToday, cardReceivedToday;

    @Autowired
    private InvoiceRepository invoiceRepository;

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
        statusTextField.setText("PAID");

        refundAndExchangeHandler.populateInvoiceTable(selectedCustomer, selectedProduct, fromDatePicker, toDatePicker,
                invoiceNoTextField, orderNoTextField, fromAmountTextField, toAmountTextField,
                cashReceivedSearchTextField, cardReceivedSearchTextField, statusTextField, invoiceTable);
        calculateAmountFields();
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
        searchInvoices();
        calculateAmountFields();
    }

    private void calculateAmountFields() {
        Double totalAmountToday = invoiceRepository.getTotalAmountToday();
        totalAmountToday = totalAmountToday == null ? 0.0 : totalAmountToday;
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Double lastDayAmount = invoiceRepository.getTotalAmountLastDay(yesterday);
        lastDayAmount = lastDayAmount == null ? 0.0 : lastDayAmount;
        YearMonth currentYearMonth = YearMonth.now();
        int year = currentYearMonth.getYear();
        int month = currentYearMonth.getMonthValue();
        Double totalAmountCurrentMonth = invoiceRepository.getTotalAmountByMonth(year, month);
        totalAmountCurrentMonth = totalAmountCurrentMonth == null ? 0.0 : totalAmountCurrentMonth;

        YearMonth lastYearMonth = currentYearMonth.minusMonths(1);
        int lastYear = lastYearMonth.getYear();
        int lastMonth = lastYearMonth.getMonthValue();
        Double totalAmountLastMonth = invoiceRepository.getTotalAmountByMonth(lastYear, lastMonth);
        totalAmountLastMonth = totalAmountLastMonth == null ? 0.0 : totalAmountLastMonth;

        Double totalCashReceivedToday = invoiceRepository.getCashReceivedToday();
        totalCashReceivedToday = totalCashReceivedToday == null ? 0.0 : totalCashReceivedToday;

        Double totalCardReceivedToday = invoiceRepository.getCardReceivedToday();
        totalCardReceivedToday = totalCardReceivedToday == null ? 0.0 : totalCardReceivedToday;

        currentDaySale.setText(Formats.getDecimalFormat().format(totalAmountToday));
        lastDaySale.setText(Formats.getDecimalFormat().format(lastDayAmount));
        currentMonthSale.setText(Formats.getDecimalFormat().format(totalAmountCurrentMonth));
        lastMonthSale.setText(Formats.getDecimalFormat().format(totalAmountLastMonth));
        cashReceivedToday.setText(Formats.getDecimalFormat().format(totalCashReceivedToday));
        cardReceivedToday.setText(Formats.getDecimalFormat().format(totalCardReceivedToday));
    }
}
