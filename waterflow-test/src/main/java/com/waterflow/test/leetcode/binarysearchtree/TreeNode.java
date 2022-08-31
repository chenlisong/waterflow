package com.waterflow.test.leetcode.binarysearchtree;

public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
          this.val = val;
          this.left = left;
          this.right = right;
    }

    @Override
    public String toString() {
        String leftToString = left == null ? "": left.toString();
        String rightToString = left == null ? "": right.toString();

        return "TreeNode{" +
                "val=" + val +
                ", left=" + leftToString +
                ", right=" + rightToString +
                '}';
    }
}
