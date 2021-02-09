package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TestCycliBarrier {
    public static void main(String[] args) {
        CyclicBarrier c = new CyclicBarrier(2, () -> {
            log.info("tast1 tast2 finish");
        });

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 3; i++) {
            executorService.submit(() -> {
                log.info("task1 begin");
                try {
                    Thread.sleep(2300);
                    c.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                log.info("task2 begin");
                try {
                    Thread.sleep(200);
                    c.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
    }
}
