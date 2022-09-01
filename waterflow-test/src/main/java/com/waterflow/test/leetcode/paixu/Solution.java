package com.waterflow.test.leetcode.paixu;

import com.waterflow.test.leetcode.singlylinked.ListNode;

public class Solution {

    public ListNode sortList(ListNode head) {
        return sort(head, null);
    }

    public ListNode sort(ListNode head, ListNode tail) {

        if(head == null) return head;

        if(head.next == tail) {
            head.next = null;
            return head;
        }

        ListNode slow = head, fast = head;
        while(fast != tail) {
            slow = slow.next; fast = fast.next;
            if(fast != tail) fast = fast.next;
        }

        ListNode mid = slow;
        ListNode left = sort(head, mid);
        ListNode right = sort(mid, tail);
        return merge(left, right);
    }

    public ListNode merge(ListNode node1, ListNode node2) {
        ListNode dist = new ListNode(0);
        ListNode distTail = dist;

        ListNode node1Tail = node1;
        ListNode node2Tail = node2;

        while(node1Tail != null || node2Tail != null) {

            if(node1Tail != null && node2Tail != null) {
                if(node1Tail.val < node2Tail.val) {
                    distTail.next = new ListNode(node1Tail.val);
                    node1Tail = node1Tail.next;
                }else {
                    distTail.next = new ListNode(node2Tail.val);
                    node2Tail = node2Tail.next;
                }
            }else if(node1Tail != null) {
                distTail.next = new ListNode(node1Tail.val);
                node1Tail = node1Tail.next;
            }else {
                distTail.next = new ListNode(node2Tail.val);
                node2Tail = node2Tail.next;
            }

            distTail = distTail.next;
        }
        return dist.next;
    }

}
