package com.waterflow.test.leetcode.stringanagram;

import java.util.HashMap;
import java.util.Map;

public class Solution {

    public int minSteps(String s, String t) {

        Map<Character, Integer> diffCnt = new HashMap<>();

        for(int i=0; i<s.length(); i++) {
            Character ch = s.charAt(i);

            if(!diffCnt.containsKey(ch)) {
                diffCnt.put(ch, 0);
            }
            diffCnt.put(ch, diffCnt.get(ch) + 1);
        }

        for(int i=0; i<t.length(); i++) {
            Character ch = t.charAt(i);

            if(diffCnt.containsKey(ch)) {
                diffCnt.put(ch, diffCnt.get(ch) - 1);
            }
        }

        int dist = 0;
        for(Integer cnt: diffCnt.values()) {
            if(cnt > 0) {
                dist += cnt;
            }
        }

        return dist;
    }

}
