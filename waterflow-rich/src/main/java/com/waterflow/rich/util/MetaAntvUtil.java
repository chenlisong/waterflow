package com.waterflow.rich.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MetaAntvUtil {

    public static List<JSONObject> convert2Antv(List<? extends Object> data, String ... fields) throws Exception{
        if(CollectionUtils.isEmpty(data) || fields == null || fields.length < 3) {
            return null;
        }

        List<JSONObject> list = new ArrayList<>();

        Class cls = data.get(0).getClass();
        Field xField = cls.getDeclaredField(fields[0]);
        xField.setAccessible(true);

        Field[] seriesField = new Field[fields.length - 1];
        for(int i=1; i<fields.length; i++) {
            Field field = cls.getDeclaredField(fields[i]);
            field.setAccessible(true);
            seriesField[i-1] = field;
        }

        for(Field yField : seriesField) {
            String fieldName = yField.getName();
            for (Object obj : data) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("x", xField.get(obj));
                jsonObject.put("y", yField.get(obj));
                jsonObject.put("series", fieldName);
                list.add(jsonObject);
            }
        }
        return list;
    }

    public static List<JSONObject> convert2AntvWithAppend(List<? extends Object> data,String append, String ... fields) throws Exception{
        if(CollectionUtils.isEmpty(data) || fields == null || fields.length < 3) {
            return null;
        }

        List<JSONObject> list = new ArrayList<>();

        Class cls = data.get(0).getClass();
        Field xField = cls.getDeclaredField(fields[0]);
        xField.setAccessible(true);

        Field[] seriesField = new Field[fields.length - 1];
        for(int i=1; i<fields.length; i++) {
            Field field = cls.getDeclaredField(fields[i]);
            field.setAccessible(true);
            seriesField[i-1] = field;
        }

        for(Field yField : seriesField) {
            String fieldName = yField.getName();
            for (Object obj : data) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("x", xField.get(obj));
                jsonObject.put("y", yField.get(obj));
                jsonObject.put("series", fieldName + append);
                list.add(jsonObject);
            }
        }
        return list;
    }

}
