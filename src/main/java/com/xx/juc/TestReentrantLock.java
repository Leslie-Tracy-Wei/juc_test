package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TestReentrantLock {
    private static ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        reentrantLock.lock();

        Thread t1 = new Thread(() -> {
            try{
                log.debug("尝试");
                reentrantLock.lockInterruptibly();
            } catch (Exception e){
                e.printStackTrace();
                log.debug("没有获得");
                return;
            }

            try{
                log.debug("log111");
            } finally {
                reentrantLock.unlock();
            }
        },"t1");
        t1.start();
        Thread.sleep(500);
        t1.interrupt();
    }
}
