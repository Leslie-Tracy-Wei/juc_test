package com.xx.juc;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Test2 {
    static int b = 10;
    public static void main(String[] args) throws Exception{
        test3();

    }


    public static void test1() throws InterruptedException {
        System.out.println("test1 run ");

        Thread t1 = new Thread("t1"){
            @SneakyThrows
            @Override
            public void run() {
                System.out.println("thread -> " + Thread.currentThread().getName());
                Thread.sleep(1000);
                b = 100;
            }
        };
        t1.start();
        // 等待t1执行完
        t1.join(1500);
        System.out.println("b -> " + b);
        System.out.println("test1 finish");
    }

    public static void test2() {

        Thread t1 = new Thread("t2"){
            @Override
            public void run() {
                while (true){
                    if (Thread.currentThread().isInterrupted()){
                        System.out.println("线程被打断了");
                        break;
                    }
                }
            }
        };

        t1.start();

        System.out.println("main 线程" );
        t1.interrupt();

    }

    public static void test3() throws InterruptedException {
        Thread t = new Thread("t2"){
            @Override
            public void run() {
                System.out.println("t2 start");
                LockSupport.park();
                System.out.println("flag -> " + Thread.currentThread().isInterrupted());
            }
        };
        t.start();

        TimeUnit.SECONDS.sleep(1);
        t.interrupt();
    }

}
