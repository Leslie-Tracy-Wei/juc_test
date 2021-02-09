package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用countDownLatch模拟开始游戏
 */
@Slf4j
public class TestLoLBeginGame {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch count = new CountDownLatch(10);
        String[] allPeople = new String[10];
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int k = i;
            executorService.submit(() -> {
                for (int j = 0; j <= 100; j++) {
                    try{
                        Thread.sleep(random.nextInt(100));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    allPeople[k] = j + "%";
                    if (j == 100){
                        count.countDown();
                    }
                    System.out.print("\r" + Arrays.toString(allPeople));
                }
            });
        }

        System.out.print("\n" + "等待进入游戏。。。");
        count.await();
        System.out.println("欢迎来到lol... " + count.getCount());
        executorService.shutdown();
    }
}
