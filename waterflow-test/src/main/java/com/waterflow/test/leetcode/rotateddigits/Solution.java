package com.waterflow.test.leetcode.rotateddigits;

public class Solution {

    public int rotatedDigits(int n) {

        int cnt = 0;
        for (int i=1; i<=n; i++) {
            if(good(i, false)) {
                cnt ++;
            }
        }

        return cnt;
    }

    public boolean good(int n, boolean flag) {
        if(n == 0) return flag;

        int dist = n % 10;
        if(dist == 3 || dist == 4 || dist == 7) return false;
        if(dist == 2 || dist == 5 || dist == 6 || dist == 9) flag = true;

        return good(n/10, flag);
    }

}
