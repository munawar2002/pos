package com.mjtech.pos.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@UtilityClass
public class Util {

    public String createOrderNo(int orderId) {
        LocalDate today = LocalDate.now();
        return String.format("%04d", orderId) + "-" +
                String.format("%02d", today.getMonth().getValue()) + "-" +
                today.getYear() % 100;
    }

    public String getResourceFilePath(String resourcePath) throws IOException {
        File file = ResourceUtils.getFile(resourcePath);
        return file.getAbsolutePath();
    }
}
