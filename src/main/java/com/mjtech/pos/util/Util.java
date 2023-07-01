package com.mjtech.pos.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class Util {

    public String createOrderNo(int orderId) {
        LocalDate today = LocalDate.now();
        return today.getYear() + String.format("%02d", today.getMonth().getValue()) +
                String.format("%02d", today.getDayOfMonth())  + String.format("%06d", orderId);
    }
}
