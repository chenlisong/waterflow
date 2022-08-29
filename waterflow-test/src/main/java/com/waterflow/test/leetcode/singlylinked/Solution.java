package com.waterflow.test.leetcode.singlylinked;

public class Solution {

    int remain;

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        //1. 异常场景，跳出
        if(l1 == null && l2 == null) {
            return null;
        }

        Integer l1Value = l1 != null ? l1.val : 0;
        Integer l2Value = l2 != null ? l2.val : 0;

        //2. 总值计算
        int value = l1Value.intValue() + l2Value.intValue() + remain;

        //3. 当前节点计算
        ListNode node = new ListNode(value % 10);

        remain = value / 10;

        //4. 判断下一个节点计算过程
        if(l1 != null || l2 != null) {

            ListNode l1Next = l1 != null ? l1.next : null;
            ListNode l2Next = l2 != null ? l2.next : null;

            ListNode nodeNext = addTwoNumbers(l1Next, l2Next);
            if(nodeNext != null) {
                node.next = nodeNext;
            }else if(remain > 0) {
                // 解决如果下一个节点为null，但是remain余数依然有值的场景
                ListNode one = new ListNode(1);
                node.next = one;
            }

            return node;
        }

        //5. 返回当前节点计算值
        if(value % 10 > 0) {
            return node;
        }

        return null;
    }

    public static void main(String[] args) {
        ListNode l1 = new ListNode(9);
        ListNode l2 = new ListNode(9);
        ListNode l3 = new ListNode(9);

        ListNode l4 = new ListNode(8);
        ListNode l5 = new ListNode(9);
        ListNode l6 = new ListNode(0);

        l1.next = l2;
        l2.next = l3;

        l4.next = l5;
        l5.next = l6;

        Solution solution = new Solution();
        ListNode node = solution.addTwoNumbers(l1, l4);
        System.out.println(node);

    }
}
