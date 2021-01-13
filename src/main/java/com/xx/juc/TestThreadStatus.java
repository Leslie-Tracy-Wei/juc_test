package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestThreadStatus {
    public static void main(String[] args) throws InterruptedException {

        // new
        Thread t1 = new Thread("t1"){
            @Override
            public void run() {
                log.info("aaaa");
            }
        };


        // RUNNABLE
        final Thread t2 = new Thread("t2"){
            @Override
            public void run() {
                while (true){

                }
            }
        };
        t2.start();

        // TIME_WAITING
        Thread t3 = new Thread("t3"){
            @Override
            public void run() {
                synchronized (TestThreadStatus.class){
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t3.start();


        // WAITING
        Thread t4 = new Thread("t4"){
            @Override
            public void run() {
                try {
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t4.start();

        // BLOCKED
        Thread t5 = new Thread("t5"){
            @Override
            public void run() {
                synchronized (TestThreadStatus.class){
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t5.start();

        // TERMINATED
        Thread t6 = new Thread("t6"){
            @Override
            public void run() {

            }
        };
        t6.start();


        Thread.sleep(500);
        log.debug(" t1 -》 " + t1.getState());
        log.debug(" t2 -》 " + t2.getState());
        log.debug(" t3 -》 " + t3.getState());
        log.debug(" t4 -》 " + t4.getState());
        log.debug(" t5 -》 " + t5.getState());
        log.debug(" t6 -》 " + t6.getState());
    }
}
