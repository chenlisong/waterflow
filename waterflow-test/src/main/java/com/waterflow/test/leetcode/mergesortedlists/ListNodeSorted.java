package com.waterflow.test.leetcode.mergesortedlists;

import com.waterflow.test.leetcode.singlylinked.ListNode;

public class ListNodeSorted implements Comparable<ListNodeSorted>{

    int val;

    ListNode node;

    public ListNodeSorted(int val, ListNode node) {
        this.val = val;
        this.node = node;
    }

    @Override
    public int compareTo(ListNodeSorted o) {
        return this.val - o.val;
    }
}
