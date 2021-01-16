package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TestWaitAndNotify {
    static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        testWaitAndNotify();
//        new Thread(() ->{
//            log.debug("doing ");
//            synchronized (lock){
//                try {
//                    lock.wait(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            log.debug("finish");
//
//        },"t1").start();
//        new Thread(() ->{
//            log.debug("doing ");
//            synchronized (lock){
//                try {
//                    lock.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            log.debug("finish");
//        },"t2").start();
//        new Thread(() ->{
//            log.debug("doing ");
//            synchronized (lock){
//                try {
//                    lock.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            log.debug("finish");
//        },"t3").start();
//
//        Thread.sleep(1000);
//        synchronized (lock){
//            lock.notify();
//        }
//        log.debug("finish");
    }


    public static void testSleepAndWait() throws InterruptedException {
        new Thread(()->{
            synchronized (lock){
                log.debug("获得锁");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("结束");
        },"t1").start();

        Thread.sleep(1);
        synchronized (lock){
            log.debug("获得锁");
        }
    }

    static boolean couyan = false;
    static boolean waimai = false;
    final static Object lock2 = new Object();
    static ReentrantLock ROOM = new ReentrantLock();
    static Condition con1 = ROOM.newCondition();
    static Condition con2 = ROOM.newCondition();
    public static void testWaitAndNotify() throws InterruptedException {
        new Thread(()->{
            ROOM.lock();
            try {
                log.debug("有没有香烟，{}",couyan);
                while (!couyan){
                    log.debug("还没有烟呢");
                    try {
                        con1.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (couyan){
                    log.debug("有香烟，开始干活{}",couyan);
                }else{
                    log.debug("还没有烟呢`");
                }
            } finally {
                ROOM.unlock();
            }
        },"小兰").start();


        new Thread(()->{
            ROOM.lock();
            try {
                log.debug("有没有外卖，{}",waimai);
                while (!waimai){
                    log.debug("还没有外卖呢");
                    try {
                        con2.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (couyan){
                    log.debug("有外卖，开始干活{}",couyan);
                }else{
                    log.debug("还没有外卖呢`");
                }
            } finally {
                ROOM.unlock();
            }
        },"小绿").start();

        Thread.sleep(12);
        new Thread(()->{
            ROOM.lock();
            try {
                log.debug("爷来送烟啦");
                couyan = true;
                con1.signal();
            } finally {
                ROOM.unlock();
            }
        },"送烟").start();

        new Thread(()->{
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ROOM.lock();
            try {
                log.debug("爷来送外卖啦");
                waimai = true;
                con2.signal();
            } finally {
                ROOM.unlock();
            }
        },"外卖").start();
    }
}
