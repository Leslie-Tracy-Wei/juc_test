package com.xx.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Test2 {
    public static void main(String[] args) throws Exception{

        Thread up = new Thread("t1") {
            @Override
            public void run() {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("wait up");
                    e.printStackTrace();
                }
            }
        };
        up.start();

        System.out.println("to wait up t1");
        Thread.sleep(1000);
        up.interrupt();
        System.out.println("interrupt");

    }

    public static void method2(int x){
        System.out.println(x + 11);
    }
}
