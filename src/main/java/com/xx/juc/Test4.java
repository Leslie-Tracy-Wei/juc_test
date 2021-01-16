package com.xx.juc;

import java.util.ArrayList;
import java.util.List;

public class Test4 {
    public static void main(String[] args) {
        List<List<Integer>> generate = generate(5);
    }
    /**
     * 杨辉三角 numRows多大 说明x就多大
     * @param numRows
     * @return
     */
    public static List<List<Integer>> generate(int numRows) {
        int round = 1;
        List<List<Integer>> result = new ArrayList<>();
        while (numRows > 0){
            List<Integer> tempResult = new ArrayList<>(round);
            if (round == 1){
                tempResult.add(1);
            }else {
                List<Integer> preList = result.get(round - 2);
                if (round == 2){
                    tempResult.add(1);
                    tempResult.add(1);
                } else{
                    for (int i = 0; i < round; i++) {
                        if (i == 0 || i == round - 1){
                            tempResult.add(1);
                        }else{
                            tempResult.add(preList.get(i-1) + preList.get(i));
                        }
                    }
                }
            }
            result.add(tempResult);
            numRows --;
            round ++;
        }
        return result;
    }
}
