package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.constant.Formats;
import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.dto.PendingInvoiceTableDto;
import com.mjtech.pos.entity.*;
import com.mjtech.pos.repository.*;
import com.mjtech.pos.service.OrderService;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderAndInvoiceHandler {

    @Autowired
    private OrderService orderService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Value("${tax.percentage}")
    private Double taxPercentage;

    public void saveOrder(Customer customer, List<OrderTableDto> orderTableDtoList,
                          TableView<PendingInvoiceTableDto> pendingInvoiceTableDtoTable,
                          TableView<OrderTableDto> invoiceTable,
                          TextField totalAmountTextField,
                          TextField gstTextField) {
        Invoice invoice = orderService.saveOrder(customer, orderTableDtoList);
        populateInvoiceTable(invoiceTable, invoice.getId(), totalAmountTextField, gstTextField);
    }

    public void populateInvoiceTable(TableView<OrderTableDto> invoiceTable, int invoiceId,
                                     TextField totalAmountTextField,
                                     TextField gstTextField) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(()-> new RuntimeException(String.format("Invoice not found with id %s", invoiceId)));

        List<OrderTableDto> orderTableDtos = createOrderTableDto(invoice);
        var columnMap = Map.of(
                "Code", "code",
                "Product Name", "productName",
                "Quantity", "quantity",
                "Price", "price",
                "Total", "total");

        FxmlUtil.populateTableView(invoiceTable, orderTableDtos, columnMap);

        double totalInvoiceAmount = orderTableDtos.stream().mapToDouble(t -> Double.parseDouble(t.getTotal())).sum();
        totalAmountTextField.setText(Formats.getDecimalFormat().format(totalInvoiceAmount));

        double percentageAmount = (totalInvoiceAmount * taxPercentage) / 100;
        gstTextField.setText(Formats.getDecimalFormat().format(percentageAmount));
    }

    private ArrayList<OrderTableDto> createOrderTableDto(Invoice invoice) {
        List<InvoiceDetail> invoiceDetails = invoiceDetailRepository.findByInvoiceId(invoice.getId());
        var orderTableDtos = new ArrayList<OrderTableDto>();
        for(InvoiceDetail invoiceDetail: invoiceDetails) {
            Product product = productRepository.findById(invoiceDetail.getProductId())
                    .orElseThrow(()-> new RuntimeException(String.format("Product not found with id %s", invoiceDetail.getProductId())));
            var dto = OrderTableDto.builder()
                    .code(product.getCode())
                    .productName(product.getName())
                    .productId(product.getId())
                    .price(Formats.getDecimalFormat().format(invoiceDetail.getAmount()))
                    .quantity(invoiceDetail.getQuantity())
                    .total(Formats.getDecimalFormat().format(invoiceDetail.getAmount() * invoiceDetail.getQuantity()))
                    .build();

            orderTableDtos.add(dto);
        }
        return orderTableDtos;
    }

    public void populatePendingInvoiceTable(TextField orderNoSearchTextField, TableView<PendingInvoiceTableDto> pendingInvoiceTableDtoTable) {
        List<Invoice> invoices = invoiceRepository.findByStatus(InvoiceStatus.CREATED);

        String orderNoSearch = orderNoSearchTextField.getText();

        var pendingInvoiceTableDtoList = mapToPendingInvoiceTableDtos(invoices, orderNoSearch);
        var columnMap = Map.of(
                "Order No.", "orderNo",
                "Status", "status",
                "Order Date", "orderDate",
                "Customer", "customer",
                "Total Amount", "amount");

        FxmlUtil.populateTableView(pendingInvoiceTableDtoTable, pendingInvoiceTableDtoList, columnMap);
    }

    private List<PendingInvoiceTableDto> mapToPendingInvoiceTableDtos(List<Invoice> invoices, String orderNoSearch) {

        List<PendingInvoiceTableDto> list = new ArrayList<>();
        for(Invoice invoice: invoices) {
            Order order = orderRepository.findById(invoice.getOrderId())
                    .orElseThrow(() -> new RuntimeException(String.format("Order not found with id %s", invoice.getOrderId())));
            Customer customer = customerRepository.findById(invoice.getCustomerId())
                    .orElseThrow(() -> new RuntimeException(String.format("Customer not found with id %s", invoice.getCustomerId())));

            PendingInvoiceTableDto dto = PendingInvoiceTableDto.builder()
                    .amount(invoice.getAmount())
                    .customer(customer.getFullName())
                    .orderDate(Formats.getSimpleDateFormat().format(order.getOrderDate()))
                    .status(invoice.getStatus().name())
                    .orderNo(order.getOrderNo())
                    .invoiceId(invoice.getId())
                    .build();
            list.add(dto);
        }

        if(StringUtils.isNotEmpty(orderNoSearch)) {
            list = list.stream()
                    .filter(dto -> dto.getOrderNo().contains(orderNoSearch))
                    .collect(Collectors.toList());
        }

        return list;
    }
}
