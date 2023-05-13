package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.constant.Formats;
import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.constant.OrderStatus;
import com.mjtech.pos.constant.PaymentType;
import com.mjtech.pos.dto.InvoiceDto;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.dto.PendingInvoiceTableDto;
import com.mjtech.pos.entity.*;
import com.mjtech.pos.repository.*;
import com.mjtech.pos.service.GeneralLedgerService;
import com.mjtech.pos.service.OrderService;
import com.mjtech.pos.service.ProductService;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GeneralLedgerService generalLedgerService;

    @Autowired
    private ProductService productService;

    @Value("${tax.percentage}")
    private Double taxPercentage;

    public Invoice saveOrder(Customer customer, List<OrderTableDto> orderTableDtoList,
                          TableView<PendingInvoiceTableDto> pendingInvoiceTableDtoTable,
                          TableView<OrderTableDto> invoiceTable,
                          TextField totalAmountTextField,
                          TextField gstTextField,
                          TextField orderRemarksTextField,
                          TextField remarksTextField) {
        String orderRemarks = orderRemarksTextField.getText();
        Invoice invoice = orderService.saveOrder(customer, orderTableDtoList, orderRemarks);
        populateInvoiceTable(invoiceTable, invoice.getId(), totalAmountTextField, gstTextField, remarksTextField);
        return invoice;
    }

    public void populateInvoiceTable(TableView<OrderTableDto> invoiceTable, int invoiceId,
                                     TextField totalAmountTextField,
                                     TextField gstTextField,
                                     TextField remarksTextField) {
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

        remarksTextField.setText(invoice.getRemarks());
    }

    public void populateOrderTableByOrderNo(String orderNo, TableView<OrderTableDto> orderTable, TextField orderRemarksTextField) {
        if(!orderTable.getItems().isEmpty()) {
            boolean result = FxmlUtil.callConfirmationAlert("Are you sure you want to load table with selected order." +
                    " Your existing data in order table will be lost");
            if(!result) {
                return;
            }
        }
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException(String.format("Order not found with orderNo %s", orderNo)));

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());

        List<OrderTableDto> orderTableDtos = mapToOrderTableDto(orderDetails, order);

        var columnMap = Map.of(
                "Code", "code",
                "Product Name", "productName",
                "Quantity", "quantity",
                "Price", "price",
                "Total", "total");

        FxmlUtil.populateTableView(orderTable, orderTableDtos, columnMap);

        orderRemarksTextField.setText(order.getRemarks());
    }

    private List<OrderTableDto> mapToOrderTableDto(List<OrderDetail> orderDetails, Order order) {
        var list = new ArrayList<OrderTableDto>();
        for(OrderDetail orderDetail : orderDetails) {
            Product product = productRepository.findById(orderDetail.getProductId())
                    .orElseThrow(() -> new RuntimeException(
                            String.format("Product not found with id %s", orderDetail.getProductId())));
            ;
            OrderTableDto dto = OrderTableDto.builder()
                    .code(product.getCode())
                    .productName(product.getName())
                    .productId(product.getId())
                    .quantity(orderDetail.getQuantity())
                    .price(Formats.getDecimalFormat().format(product.getSellPrice()))
                    .total(Formats.getDecimalFormat().format(product.getSellPrice() * orderDetail.getQuantity()))
                    .existingOrder(true)
                    .orderId(order.getId())
                    .build();
            list.add(dto);
        }
        return list;
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
        List<Invoice> invoices = invoiceRepository.findByStatus(InvoiceStatus.CREATED).stream()
                .sorted(Comparator.comparing(Invoice::getInvoiceDate).reversed())
                .collect(Collectors.toList());

        String orderNoSearch = orderNoSearchTextField.getText();

        var pendingInvoiceTableDtoList = mapToPendingInvoiceTableDtos(invoices, orderNoSearch);
        var columnMap = Map.of(
                "Order No.", "orderNo",
                "Status", "status",
                "Order Date", "orderDate",
                "Customer", "customer",
                "Total Amount", "amount",
                "Remarks", "remarks");

        FxmlUtil.populateTableView(pendingInvoiceTableDtoTable, pendingInvoiceTableDtoList, columnMap);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void generateInvoice(InvoiceDto invoiceDto) {
        Invoice invoice = invoiceRepository.findById(invoiceDto.getInvoiceId())
                .orElseThrow(() -> new RuntimeException(String.format("Invoice not found with id %s", invoiceDto.getInvoiceId())));

        // update invoice fields
        updateInvoiceFields(invoiceDto, invoice);
        updateOrderFields(invoice);

        // update product quantities
        List<InvoiceDetail> invoiceDetails = invoiceDetailRepository.findByInvoiceId(invoice.getId());
        invoiceDetails.forEach(invoiceDetail ->
                productService.updateProductQuantity(invoiceDetail.getProductId(),
                -invoiceDetail.getQuantity()));

        // create ledger entries
        generalLedgerService.createInvoiceSellLedgerEntry(invoice);


        // TODO: print invoice
    }

    private void updateOrderFields(Invoice invoice) {
        Order order = orderRepository.findById(invoice.getOrderId())
                .orElseThrow(() -> new RuntimeException(String.format("Order not found with id %s", invoice.getOrderId())));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    private void updateInvoiceFields(InvoiceDto invoiceDto, Invoice invoice) {
        invoice.setDiscountAmount(invoiceDto.getDiscountAmount());
        invoice.setDiscountPercentage(invoiceDto.getDiscountPercent());
        invoice.setTotalDiscount(invoiceDto.getDiscountTotal());

        double balanceAmount = invoiceDto.getBalanceAmount();
        if(invoiceDto.getPaymentType() == PaymentType.CASH) {
            invoice.setTotalReceived(invoiceDto.getCashReceived());
            invoice.setCardReceived(0.0);
        } else if(invoiceDto.getPaymentType() == PaymentType.CARD) {
            double totalReceived = balanceAmount;
            invoice.setTotalReceived(totalReceived);
            invoice.setCardReceived(totalReceived);
            balanceAmount = 0;
        } else if(invoiceDto.getPaymentType() == PaymentType.CASH_AND_CARD) {
            double receivedAmount = invoiceDto.getCashReceived() + balanceAmount;
            invoice.setCardReceived(balanceAmount);
            invoice.setTotalReceived(receivedAmount);
            balanceAmount = 0;
        }

        invoice.setCashReceived(invoiceDto.getCashReceived());
        invoice.setBalanceAmount(balanceAmount);
        invoice.setTotalAmount(invoiceDto.getTotalAmount());
        invoice.setRemarks(invoiceDto.getRemarks());
        invoice.setAmount(invoiceDto.getAmount());
        invoice.setGst(invoiceDto.getGst());
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setInvoiceDate(new Date());
        invoice.setStatusChangeDate(new Date());
        invoice.setTaxPercentage(taxPercentage);
        invoice.setPaymentType(invoiceDto.getPaymentType());


        invoiceRepository.save(invoice);
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
                    .remarks(invoice.getRemarks())
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
