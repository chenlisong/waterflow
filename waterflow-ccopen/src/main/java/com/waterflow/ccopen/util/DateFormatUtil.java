package com.waterflow.ccopen.util;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.RoundingMode;

public class DateFormatUtil {

    public static double format(double value) {
        return NumberUtils.toScaledBigDecimal(value, Integer.valueOf(1), RoundingMode.HALF_UP).doubleValue();
    }

}
