package com.waterflow.test.leetcode.palindromenumber;

public class Solution {

    public boolean isPalindrome(int x) {

        if(x < 0) {
            return false;
        }

        String word = x + "";

        String palindrome = "";
        for(int i=word.length()-1; i>=0; i--) {
            palindrome += word.charAt(i);
        }

        return word.equals(palindrome);
    }

}
