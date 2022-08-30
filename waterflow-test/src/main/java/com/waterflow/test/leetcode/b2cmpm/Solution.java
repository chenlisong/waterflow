package com.waterflow.test.leetcode.b2cmpm;

import java.util.Arrays;

/**
 * 给定一个由 0 和 1 组成的矩阵 mat ，请输出一个大小相同的矩阵，其中每一个格子是 mat 中对应位置元素到最近的 0 的距离。
 *
 * 两个相邻元素间的距离为 1 。
 *https://leetcode.cn/problems/2bCMpM
 */
public class Solution {

    public int[][] b2cmpm(int[][] mat) {

        int m = mat.length, n = mat[0].length;

        int [][] dist = new int[m][n];

        for(int i=0;i< m; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE - 1);
        }

        for(int i=0;i<m; i++) {
            for(int j=0; j<n; j++) {
                if(mat[i][j] == 0) {
                    dist[i][j] = 0;
                }
            }
        }

        // 左、 上
        for(int i=0; i<m; i++) {
            for(int j =0; j<n; j++) {

                if(i-1 >= 0)
                dist[i][j] = Math.min(dist[i][j], dist[i-1][j] + 1);

                if(j-1 >= 0)
                    dist[i][j] = Math.min(dist[i][j], dist[i][j-1] + 1);
            }
        }

        //左 下
        for(int i=m-1; i>=0; i--) {
            for(int j =0; j<n; j++) {

                if(i+1 < m)
                    dist[i][j] = Math.min(dist[i][j], dist[i+1][j] + 1);

                if(j-1 >= 0)
                    dist[i][j] = Math.min(dist[i][j], dist[i][j-1] + 1);
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = n - 1; j >= 0; j--) {
                if (i - 1 >= 0) {
                    dist[i][j] = Math.min(dist[i][j], dist[i - 1][j] + 1);
                }
                if (j + 1 < n) {
                    dist[i][j] = Math.min(dist[i][j], dist[i][j + 1] + 1);
                }
            }
        }
        for(int i=m-1; i>=0; i--) {
            for(int j =n-1; j>=0; j--) {

                if(i+1 < m)
                    dist[i][j] = Math.min(dist[i][j], dist[i+1][j] + 1);

                if(j+1 < n)
                    dist[i][j] = Math.min(dist[i][j], dist[i][j+1] + 1);
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        int[][] mat = {{0, 0, 0}, {0, 1, 0}, {0, 0, 0}};
        System.out.println(new Solution().b2cmpm(mat));
    }

}
