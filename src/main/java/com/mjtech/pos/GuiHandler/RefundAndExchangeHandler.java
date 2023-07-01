package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.constant.Formats;
import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.dto.InvoiceTableDto;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.entity.Customer;
import com.mjtech.pos.entity.Invoice;
import com.mjtech.pos.entity.InvoiceDetail;
import com.mjtech.pos.mapper.DtoMapper;
import com.mjtech.pos.repository.InvoiceDetailRepository;
import com.mjtech.pos.repository.InvoiceRepository;
import com.mjtech.pos.service.OrderService;
import com.mjtech.pos.service.RefundService;
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
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RefundAndExchangeHandler {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    public void populateInvoiceTable(Customer selectedCustomer, ProductDto selectedProduct, DatePicker fromDatePicker,
                                     DatePicker toDatePicker, TextField invoiceNoTextField, TextField orderNoTextField,
                                     TextField fromAmountTextField, TextField toAmountTextField,
                                     TextField cashReceivedSearchTextField, TextField cardReceivedSearchTextField,
                                     TextField statusTextField,
                                     TableView<InvoiceTableDto> invoiceTable) {
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
            String status = statusTextField.getText();

            List<Invoice> invoices = orderService.searchInvoice(customerId, productId,
                            fromDate, toDate, orderNo, invoiceNo, fromAmount, toAmount,
                            cashReceived, cardReceived, InvoiceStatus.valueOf(status))
                    .stream()
                    .sorted(Comparator.comparing(Invoice::getInvoiceDate).reversed())
                    .toList();


            var pendingInvoiceTableDtoList = DtoMapper.mapToPendingInvoiceTableDtos(invoices, "", true);
            var columnMap = Map.of(
                    "Order No.", "orderNo",
                    "Status", "status",
                    "Order Date", "orderDate",
                    "Customer", "customer",
                    "Total Amount", "amount",
                    "Total Discount", "totalDiscount",
                    "Payment Type", "paymentType",
                    "Cash Rcvd", "cashReceived",
                    "Card Rcvd", "cardReceived");

            FxmlUtil.populateTableView(invoiceTable, pendingInvoiceTableDtoList, columnMap);
        } catch (Exception e) {
            String errorMessage = String.format("ReceptionForm: Failed while populating pending invoice table with orderNo %s",
                    "");
            log.error(errorMessage, e);
            FxmlUtil.callErrorAlert("Failed while populating pending invoice table. Please contact administrator!");
            throw new RuntimeException(errorMessage, e);
        }
    }


    public void populateInvoiceDetailTable(TableView<OrderTableDto> invoiceDetailTable, Invoice invoice) {
        try {
            List<InvoiceDetail> invoiceDetails = invoice.getInvoiceDetails();

            List<OrderTableDto> orderTableDtos = DtoMapper.mapToOrderTableDto(invoice, invoiceDetails);

            var columnMap = Map.of(
                    "Code", "code",
                    "Product Name", "productName",
                    "Quantity", "quantity",
                    "Price", "price",
                    "Total", "total",
                    "Select", "selected");

            FxmlUtil.populateTableView(invoiceDetailTable, orderTableDtos, columnMap);
        } catch (Exception e) {
            String errorMessage = String.format("RefundForm: Failed while populating invoice detail table for invoiceId %d", invoice.getId());
            log.error(errorMessage, e);
            FxmlUtil.callErrorAlert("Failed while populating invoice items table. Please contact administrator!");
            throw new RuntimeException(errorMessage, e);
        }
    }

    public Invoice saveRefundOrder(TableView<OrderTableDto> invoiceDetailTable, Invoice selectedInvoice) {
        try {
            List<OrderTableDto> selectedProducts = invoiceDetailTable.getItems().stream()
                    .filter(OrderTableDto::isSelected)
                    .toList();

            return refundService.saveRefundOrder(
                    selectedInvoice.getCustomer(),
                    selectedProducts,
                    selectedInvoice);
        } catch (Exception e) {
            String errorMessage = String.format("RefundForm: Failed while saving refund order for %d products",
                    invoiceDetailTable.getItems().size());
            log.error(errorMessage, e);
            FxmlUtil.callErrorAlert("Failed while saving refund order. Please contact administrator!");
            throw new RuntimeException(errorMessage, e);
        }
    }

    public void populateRefundTable(TableView<OrderTableDto> refundTable, Invoice invoice) {

        List<OrderTableDto> orderTableDtos = DtoMapper.mapToOrderTableDto(invoice, invoice.getInvoiceDetails());

        var columnMap = Map.of(
                "Code", "code",
                "Product Name", "productName",
                "Quantity", "quantity",
                "Price", "price",
                "Total", "total");

        FxmlUtil.populateTableView(refundTable, orderTableDtos, columnMap);
    }

    public void generateRefundInvoice(Invoice refundInvoice, TableView<OrderTableDto> refundTable) {



    }
}
