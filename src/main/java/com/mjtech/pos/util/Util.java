package com.mjtech.pos.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class Util {

    public String createOrderNo(int orderId) {
        LocalDate today = LocalDate.now();
        return String.format("%04d", orderId) + "-" +
                String.format("%02d", today.getMonth().getValue()) + "-" +
                today.getYear() % 100;
    }
}
