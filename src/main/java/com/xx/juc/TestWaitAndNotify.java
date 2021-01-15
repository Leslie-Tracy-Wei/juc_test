package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

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
    public static void testWaitAndNotify() throws InterruptedException {
        new Thread(()->{
            synchronized (lock2){
                log.debug("有眼没？");
                while (!couyan){
                    log.debug("没有烟");
                    try {
                        lock2.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (couyan){
                    log.debug("youyanle ganhuo");
                }
            }
        },"小兰").start();


        new Thread(()->{
            synchronized (lock2){
                log.debug("有外卖没？");
                while (!waimai){
                    log.debug("没有外卖");
                    try {
                        lock2.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (waimai){
                    log.debug("you外卖了le ganhuo");
                }
            }
        },"小绿").start();

        Thread.sleep(12);
        synchronized (lock2){
            waimai = !waimai;
            lock2.notifyAll();
        }
    }
}
