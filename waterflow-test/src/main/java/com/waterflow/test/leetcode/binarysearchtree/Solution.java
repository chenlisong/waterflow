package com.waterflow.test.leetcode.binarysearchtree;

/**
 * 1008. 前序遍历构造二叉搜索树
 */
public class Solution {

    public TreeNode bstFromPreorder(int[] preorder) {
        if(preorder == null || preorder.length <= 0) return null;

        return dfs(preorder, 0, preorder.length-1);
    }

    private TreeNode dfs(int[] array, int left, int right) {
        //1. 定义边界
        if(left > right) {
            return null;
        }

        TreeNode root = new TreeNode(array[left]);
        //2. 命中结果返回
        if(left == right) {
            return root;
        }

        //3. 寻找比左边第一个节点小的最后一个位置，位置存储在l
        int l = left, r = right;
        while(l < r) {
            int mid = (r - l + 1 ) / 2 + l;
            if (array[mid] < array[left]) {
                // [mid - r]
                l = mid;
            }else {
                // [l - (mid-1)]
                r = mid - 1;
            }
        }

        //4. 递归遍历左节点
        TreeNode leftNode = dfs(array, left+1, l);
        //5. 递归遍历右节点
        TreeNode rightNode = dfs(array, l+1, right);
        root.left = leftNode; root.right = rightNode;
        return root;
    }
}
