package com.example.demo;

import com.waterflow.test.leetcode.binarysearchtree.TreeNode;
import com.waterflow.test.leetcode.rotateddigits.Solution;
import net.minidev.json.JSONArray;
import org.assertj.core.util.DateUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

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

    com.waterflow.test.leetcode.rotateddigits.Solution rotateddigitsSolution
                = new Solution();
    @Test
    public void rotateddigitsTest() {
        int cnt = rotateddigitsSolution.rotatedDigits(10);
        System.out.println(cnt);
    }

    com.waterflow.test.leetcode.stringanagram.Solution stringAnagram
                = new com.waterflow.test.leetcode.stringanagram.Solution();

    @Test
    public void stringAnagramTest() {
        int cnt = stringAnagram.minSteps("bab", "aba");
        logger.info("cnt is {}", cnt);
    }

    com.waterflow.test.leetcode.hhvs.Solution hhvsSolution
            = new com.waterflow.test.leetcode.hhvs.Solution();
    @Test
    public void hhvsTest() {
        String[] dist = hhvsSolution.permutation("abc");
        logger.info("hhvs dist is {}", dist);
    }

    com.waterflow.test.leetcode.kfactorofn.Solution kfactorfn
            = new com.waterflow.test.leetcode.kfactorofn.Solution();
    @Test
    public void kfactorfnTest() {
        System.out.println(new com.waterflow.test.leetcode.kfactorofn.Solution().kthFactor(12, 3));
    }

    com.waterflow.test.leetcode.maxnum.Solution maxnumSolution
            = new com.waterflow.test.leetcode.maxnum.Solution();
    @Test
    public void maxnumTest() {
        logger.info("max num test, 96669 target num is {}", maxnumSolution.maximum69Number(96669));
    }

    com.waterflow.test.leetcode.mergeintervals.Solution mergeIntervalSolution
            = new com.waterflow.test.leetcode.mergeintervals.Solution();
    @Test
    public void mergeIntervalSolutionTest() {
//        int[][] dist = mergeIntervalSolution.merge(new int[][] {{1,3}, {2,6}, {8,10}, {15,18}});
        int[][] dist = mergeIntervalSolution.merge(new int[][] {{1,4}, {0,5}});
        logger.info("dist is {}", dist);
    }


}
