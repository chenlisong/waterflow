package com.waterflow.rich.util;

import com.alibaba.fastjson.serializer.ValueFilter;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberFilter {

    static ValueFilter doubleFilter = new ValueFilter() {
        @Override
        public Object process(Object o, String s, Object value){
            try{
                if(value instanceof BigDecimal || value instanceof Double) {
                    return NumberUtils.toScaledBigDecimal((double)value, Integer.valueOf(2), RoundingMode.HALF_UP).doubleValue();
                }
            }catch (Exception e) {
            }
            return value;
        }
    };

    private NumberFilter(){}

    public static ValueFilter defaultDouble() {
        return doubleFilter;
    }
}
