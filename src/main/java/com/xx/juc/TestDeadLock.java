package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDeadLock {
    public static void main(String[] args) {

        Object a = new Object();
        Object b = new Object();
        new Thread(() ->{
            synchronized (a){
                log.debug("持有a");
                try {
                    Thread.sleep(11);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (b){
                    log.debug("持有b");
                }
            }
        }).start();
        new Thread(() ->{
            synchronized (b){
                log.debug("持有b");
                try {
                    Thread.sleep(11);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (a){
                    log.debug("持有a");
                }
            }
        }).start();


    }
}
