package com.xx;

import java.util.*;

public class test {
    public static void main(String[] args) {
        Map<String,Integer> te = new HashMap<>();
        Map<String,Integer> result = new HashMap<>();
        te.put(" aaa",1);
        te.put(" bbb",2);
        te.put(" ccc",3);
        te.put(" dddd",3);
        te.keySet().forEach(t -> {
            t = t.trim();

        });
        te.forEach(
                (k,v) -> {
                    result.put(k.trim(),v);
                }
        );
        result.forEach(
                (k,v) -> {
                    System.out.println(k.length() + "," + k + v);
                }
        );
    }

    public List<Integer> findDisappearedNumbers(int[] nums) {
        List<Integer> result = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1){
                result.add(i + 1);
            }
        }
        return result;
    }
    private static String[] getC(String[] m, String[] n)
    {
        // 将较长的数组转换为set
        Set<String> set = new HashSet<String>(Arrays.asList(m.length > n.length ? m : n));

        // 遍历较短的数组，实现最少循环
        for (String i : m.length > n.length ? n : m)
        {
            // 如果集合里有相同的就删掉，如果没有就将值添加到集合
            if (set.contains(i))
            {
                set.remove(i);
            } else
            {
                set.add(i);
            }
        }

        String[] arr = {};
        return set.toArray(arr);
    }

    private static Map<String, Integer> findRepetition(String[] arr){
        Map<String, Integer> map = new HashMap<String, Integer>();
        if(arr == null || arr.length <= 0){
            return null;
        }
        for(int i = 0; i < arr.length; i ++){
            if(map.containsKey(arr[i])){
                map.put(arr[i], map.get(arr[i])+1);
            }else{
                map.put(arr[i], 1);
            }
        }
        return map;
    }
}
