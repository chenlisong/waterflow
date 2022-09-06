package com.waterflow.test.leetcode.maxnum;

public class Solution {

    public int maximum69Number (int num) {
        String numStr = num + "";
        char[] dist = new char[numStr.length()];

        int index = 0;
        boolean first = true;
        while(index < numStr.length()) {
            char temp = numStr.charAt(index);
            if(temp == '6' && first) {
                temp = '9';
                first = false;
            }
            dist[index] = temp;
            index++;
        }

        return Integer.parseInt(new String(dist));
    }
}
