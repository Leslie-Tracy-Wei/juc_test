package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

@Slf4j
public class TestCountdownLatch {
    public static void main(String[] args) {
        CountDownLatch l  = new CountDownLatch(3);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.submit(() -> {
            log.debug("begin 1 ...");
            try {
                sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            l.countDown();
            log.debug("end 1 ... {}",l.getCount());
        });

        executorService.submit(() -> {
            log.debug("begin 2 ...");
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            l.countDown();
            log.debug("end 2 . .. {}",l.getCount());
        });

        executorService.submit(() -> {
            log.debug("begin 3 ...");
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            l.countDown();
            log.debug("end 3 ... {}",l.getCount());
        });
        executorService.submit(() -> {
            log.debug("begin 4 ...");
            try {
                l.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("end 4 ... {}",l.getCount());
        });
    }
}
