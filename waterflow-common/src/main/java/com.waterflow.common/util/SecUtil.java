package com.waterflow.common.util;

import java.util.Random;

public class SecUtil {

    private static int[] key = new int[] {6,11,15,1,19,20,7,18,4,17,23};

    public static String sec(long id) {
        Random random = new Random();

        // 00000090600
        String idStr = id + "";
        int diffLen = 11-idStr.length();

        for(int i=0;i<diffLen; i++) {
            idStr = "0" + idStr;
        }

        Long[] output = new Long[24];

        for(int i=0; i<idStr.length(); i++) {
            output[key[i]] = Long.parseLong(idStr.charAt(i) + "");
        }

        StringBuilder reuslt = new StringBuilder();

        for(int i=0;i<output.length; i++) {
            if(output[i] == null) {
                output[i] = (long)random.nextInt(10);
            }

            reuslt.append(output[i]);
        }
        return reuslt.toString();
    }

    public static long dec(String str) {
        if(str == null || str.length() != 24) {
            return 0;
        }

        StringBuilder result = new StringBuilder();
        for(int i=0; i<key.length; i++) {
            result.append(str.charAt(key[i]) + "");
        }

        return Long.parseLong(result.toString());
    }

}
