package com.waterflow.test.leetcode.mergesortedlists;

import com.waterflow.test.leetcode.singlylinked.ListNode;

import java.util.PriorityQueue;

public class Solution {

    public ListNode mergeKLists(ListNode[] lists) {
        //1. 定义优先队列。其中：key按照Compatable排序
        PriorityQueue<ListNodeSorted> queue = new PriorityQueue<ListNodeSorted>();

        for(ListNode node: lists) {
            queue.offer(new ListNodeSorted(node.val, node));
        }

        //2. 定义结果
        ListNode dist = new ListNode(0);
        //3. 定义游标
        ListNode loop = dist;

        //4. 当有序队列持续产生数据时
        while(!queue.isEmpty()) {

            //5. 队列移除数据
            ListNodeSorted nodeSorted = queue.poll();

            loop.next = nodeSorted.node;
            loop = loop.next;

            //6. 如果next不为空，该队列补充next到PriorityQueue中
            if(nodeSorted.node.next != null)
            queue.offer(new ListNodeSorted(nodeSorted.node.next.val, nodeSorted.node.next));
        }

        return dist.next;
    }



    public static void main(String[] args) {
        ListNode l1 = new ListNode(1);
        l1.next = new ListNode(4);
        l1.next.next = new ListNode(5);

        ListNode l2 = new ListNode(1);
        l2.next = new ListNode(3);
        l2.next.next = new ListNode(4);

        ListNode l3 = new ListNode(2);
        l3.next = new ListNode(6);

        System.out.println(new Solution().mergeKLists(new ListNode[] {l1, l2, l3}));
    }

}
