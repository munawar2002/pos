package com.mjtech.pos.mapper;

import com.mjtech.pos.constant.Formats;
import com.mjtech.pos.dto.GenericFromDto;
import com.mjtech.pos.dto.InvoiceTableDto;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.entity.*;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@UtilityClass
public class DtoMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public List<GenericFromDto> mapToGenericFormDtos(List<?> objects) {
        List<GenericFromDto> list = new ArrayList<>();
        for(Object object: objects) {
            GenericFromDto dto = modelMapper.map(object, GenericFromDto.class);
            list.add(dto);
        }

        return list;
    }

    public List<InvoiceTableDto> mapToPendingInvoiceTableDtos(List<Invoice> invoices, String orderNoSearch, boolean addTotalAmount) {

        List<InvoiceTableDto> list = new ArrayList<>();
        for(Invoice invoice: invoices) {
            Order order = invoice.getOrder();
            Customer customer = invoice.getCustomer();

            Double finalAmount = addTotalAmount ? invoice.getTotalAmount() : invoice.getAmount();
            InvoiceTableDto dto = InvoiceTableDto.builder()
                    .amount(finalAmount)
                    .customer(customer.getFullName())
                    .orderDate(Formats.getSimpleDateFormat().format(order.getOrderDate()))
                    .status(invoice.getStatus().name())
                    .orderNo(order.getOrderNo())
                    .invoiceId(invoice.getId())
                    .remarks(invoice.getRemarks())
                    .paymentType(invoice.getPaymentType() == null ? "" : invoice.getPaymentType().name())
                    .totalDiscount(invoice.getTotalDiscount())
                    .cashReceived(invoice.getCashReceived())
                    .cardReceived(invoice.getCardReceived())
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

    public ArrayList<OrderTableDto> mapToOrderTableDto(Invoice invoice, List<InvoiceDetail> invoiceDetails) {
        var orderTableDtos = new ArrayList<OrderTableDto>();
        for(InvoiceDetail invoiceDetail: invoiceDetails) {
            Product product = invoiceDetail.getProduct();
            var dto = OrderTableDto.builder()
                    .code(product.getCode())
                    .productName(product.getName())
                    .productId(product.getId())
                    .price(Formats.getDecimalFormat().format(invoiceDetail.getAmount()))
                    .quantity(invoiceDetail.getQuantity())
                    .total(Formats.getDecimalFormat().format(invoiceDetail.getAmount() * invoiceDetail.getQuantity()))
                    .selected(new SimpleBooleanProperty(false))
                    .build();

            orderTableDtos.add(dto);
        }
        return orderTableDtos;
    }

}
