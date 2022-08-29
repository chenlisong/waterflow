package com.waterflow.test.leetcode.singlylinked;

public class ListNode {

    int val;
    ListNode next;

    public ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

    public ListNode(int val) {
        this.val = val;
    }

    @Override
    public String toString() {
        String nextVal = next == null ? "" : next.toString();

        return "ListNode{" +
                "val=" + val +
                ", next=" + nextVal +
                '}';
    }
}
