package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

@Slf4j
public class TestSemaphore {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 10; i++) {

            new Thread( () ->{
                try {
                    semaphore.acquire();
                    log.info("run....");
                    sleep(1000);
                    log.info("finish");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }).start();
        }
    }
}
