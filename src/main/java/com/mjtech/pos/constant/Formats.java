package com.mjtech.pos.constant;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

@UtilityClass
public class Formats {
    DecimalFormat df;

    SimpleDateFormat dateFormat;

    /**
     * The decimal formatter that will convert the decimal to two precision point.
     * @return DecimalFormat
     */
    public DecimalFormat getDecimalFormat() {
        if (df == null) {
            return new DecimalFormat("#.##");
        }
        return df;
    }


    /**
     * The date formatter that will convert date to "yyyy-MM-dd HH:mm:ss" format.
     * @return DecimalFormat
     */
    public SimpleDateFormat getSimpleDateFormat() {
        if (dateFormat == null) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return dateFormat;
    }


}
