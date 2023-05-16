package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.dto.InvoiceDto;
import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.entity.Customer;
import com.mjtech.pos.entity.Invoice;
import com.mjtech.pos.service.OrderService;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class RefundAndExchangeHandler {

    @Autowired
    private OrderService orderService;

    public void populateInvoiceTable(Customer selectedCustomer, ProductDto selectedProduct, DatePicker fromDatePicker,
                                     DatePicker toDatePicker, TextField invoiceNoTextField, TextField orderNoTextField,
                                     TextField fromAmountTextField, TextField toAmountTextField,
                                     TextField cashReceivedSearchTextField, TextField cardReceivedSearchTextField,
                                     TableView<InvoiceDto> invoiceTable) {
        try {

            String invoiceNo = invoiceNoTextField.getText();
            String orderNo = orderNoTextField.getText();
            Date fromDate = fromDatePicker.getValue() == null ? null :
                    Date.from(fromDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date toDate = toDatePicker.getValue() == null ? null :
                    Date.from(toDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Double fromAmount = StringUtils.isEmpty(fromAmountTextField.getText()) ? null : Double.parseDouble(fromAmountTextField.getText());
            Double toAmount = StringUtils.isEmpty(toAmountTextField.getText()) ? null : Double.parseDouble(toAmountTextField.getText());
            Double cashReceived = StringUtils.isEmpty(cashReceivedSearchTextField.getText()) ? null : Double.parseDouble(cashReceivedSearchTextField.getText());
            Double cardReceived = StringUtils.isEmpty(cardReceivedSearchTextField.getText()) ? null : Double.parseDouble(cardReceivedSearchTextField.getText());
            Integer customerId = selectedCustomer == null ? null : selectedCustomer.getId();
            Integer productId = selectedProduct == null ? null : selectedProduct.getId();


            List<Invoice> invoices = orderService.searchInvoice(customerId, productId,
                            fromDate, toDate, orderNo, invoiceNo, fromAmount, toAmount,
                            cashReceived, cardReceived, InvoiceStatus.PAID)
                    .stream()
                    .sorted(Comparator.comparing(Invoice::getInvoiceDate).reversed())
                    .toList();

            log.info("Size: "+ invoices.size());
//            String orderNoSearch = orderNoSearchTextField.getText();
//
//            var pendingInvoiceTableDtoList = mapToPendingInvoiceTableDtos(invoices, orderNoSearch);
//            var columnMap = Map.of(
//                    "Order No.", "orderNo",
//                    "Status", "status",
//                    "Order Date", "orderDate",
//                    "Customer", "customer",
//                    "Total Amount", "amount",
//                    "Remarks", "remarks");
//
//            FxmlUtil.populateTableView(pendingInvoiceTableDtoTable, pendingInvoiceTableDtoList, columnMap);
        } catch (Exception e) {
            String errorMessage = String.format("ReceptionForm: Failed while populating pending invoice table with orderNo %s",
                    "");
            log.error(errorMessage, e);
            FxmlUtil.callErrorAlert("Failed while populating pending invoice table. Please contact administrator!");
            throw new RuntimeException(errorMessage, e);
        }
    }


}
