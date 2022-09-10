package com.waterflow.test.leetcode.mergeintervals;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    public int[][] merge(int[][] intervals) {
        if(intervals == null || intervals.length <= 0) {
            return intervals;
        }

        List<int[]> dist = new ArrayList<>();

        intervalLoop: for(int[] unit: intervals) {

            if(dist.size() == 0) {
                dist.add(unit);
                continue intervalLoop;
            }

            List<int[]> conflict = new ArrayList<>();
            for(int[] distUnit: dist) {
                if(intervalArray(unit, distUnit)) {
                    conflict.add(distUnit);
                }
            }

            if(conflict.size() == 0) {
                dist.add(unit);
                continue intervalLoop;
            }

            int[] mergeArray = unit;
            int index = 0;
            while(index < conflict.size()) {
                int[] conflictUnit = conflict.get(index);
                mergeArray = merge(mergeArray, conflictUnit);
                index++;

                dist.remove(conflictUnit);
            }
            dist.add(mergeArray);
        }
        return dist.toArray(new int[][]{});
    }

    private boolean intervalArray(int[] a, int[] b) {
        if(a != null && b != null && a.length == 2 && b.length == 2) {
            if((a[0] >= b[0] && a[0] <= b[1]) || (a[1] >= b[0] && a[1] <= b[1])
                || (a[0] <= b[0] && a[1] >= b[1])) {
                return true;
            }
        }
        return false;
    }

    private int[] merge(int[] a, int[] b) {
        int[] dist = new int[2];

        if(a[0] < b[0]) {
            dist[0] = a[0];
        }else {
            dist[0] = b[0];
        }

        if(a[1] > b[1]) {
            dist[1] = a[1];
        }else {
            dist[1] = b[1];
        }
        return dist;
    }

}
