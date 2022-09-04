package com.waterflow.test.leetcode.kfactorofn;

public class Solution {

    public int kthFactor(int n, int k) {

        int cnt = 0;
        for(int factor=1; factor<=n; factor++) {
            if(n % factor == 0) {
                ++cnt;
            }

            if(k == cnt) {
                return factor;
            }
        }

        return -1;
    }

}
