package com.example.demo;

import com.waterflow.test.leetcode.binarysearchtree.TreeNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LeetCodeTest {

    com.waterflow.test.leetcode.binarysearchtree.Solution searchTreeSolution
                = new com.waterflow.test.leetcode.binarysearchtree.Solution();

    Logger logger = LoggerFactory.getLogger(LeetCodeTest.class);

    @Test
    public void searchTreeTest() {
        TreeNode node = searchTreeSolution.bstFromPreorder(new int[] {8, 5, 1, 7, 10, 12});
        logger.info(node.toString());

    }

}
