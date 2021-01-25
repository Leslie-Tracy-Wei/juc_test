package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestExecutors {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<String> submit = executorService.submit(() -> {
            log.debug("task 1 running...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task 1 finish..");
            return "1";
        });

        Future<String> submit2 = executorService.submit(() -> {
            log.debug("task 2 running...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task 2 finish..");
            return "2";
        });

        Future<String> submit3 = executorService.submit(() -> {
            log.debug("task 3 running...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task 3 finish..");
            return "3";
        });

        log.debug("shutdown");
//        executorService.shutdown();
//        executorService.awaitTermination(3, TimeUnit.SECONDS);
        List<Runnable> runnables = executorService.shutdownNow();
        log.debug("runnables -> {}",runnables );

        log.debug("finish");
//        Future<String> submit4 = executorService.submit(() -> {
//            log.debug("task 4 running...");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            log.debug("task 4 finish..");
//            return "3";
//        });

    }
}
