package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Test3 {
    static Object lock = new Object();
    static boolean flag = false;
    static Thread a1;
    static Thread a2;
    static Thread a3;
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                while (!flag) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                log.debug("t1");
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                log.debug("t2");
                flag = true;
                lock.notifyAll();
            }
        }, "t2");

        t1.start();
        t2.start();

        Thread t3 = new Thread(() -> {
            LockSupport.park();
            log.debug("t3");
        }, "t3");

        Thread t4 = new Thread(() -> {

            log.debug("t4");
            LockSupport.unpark(t3);
        }, "t4");

        t3.start();
        t4.start();

        WaitAndNotify waitAndNotify = new WaitAndNotify(1, 5);
        new Thread(() -> {
            waitAndNotify.print("a",1,2);
        }, "t4").start();
        new Thread(() -> {
            waitAndNotify.print("b",2,3);
        }, "t5").start();
        new Thread(() -> {
            waitAndNotify.print("c",3,1);
        }, "t6").start();


        LockAndUnLock lockAndUnLock = new LockAndUnLock(5);

        Condition a = lockAndUnLock.newCondition();
        Condition b = lockAndUnLock.newCondition();
        Condition c = lockAndUnLock.newCondition();

        new Thread(() -> {
            lockAndUnLock.print("a",a,b);
        }, "t7").start();
        new Thread(() -> {
            lockAndUnLock.print("b",b,c);
        }, "t8").start();
        new Thread(() -> {
            lockAndUnLock.print("c",c,a);
        }, "t9").start();

        Thread.sleep(300);
        lockAndUnLock.lock();
        try {
            a.signal();
        }finally {
            lockAndUnLock.unlock();
        }

        PackAndUnPack p = new PackAndUnPack(10);
        a1 = new Thread(() -> {
            p.print("a",a2);
        }, "t10");
        a2 = new Thread(() -> {
            p.print("b",a3);
        }, "t11");
        a3 = new Thread(() -> {
            p.print("c",a1);
        }, "t12");
        a1.start();;
        a2.start();
        a3.start();
        LockSupport.unpark(a1);
    }
}


class WaitAndNotify{
    private int actualNumber;
    private int loop;

    public WaitAndNotify(int actualNumber,int loop){
        this.actualNumber = actualNumber;
        this.loop = loop;
    }


    public void print(String str,int waiting,int nextFlag){
        for (int i = 0; i < loop; i++) {
            synchronized (this){
                while (actualNumber != waiting){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(str);
                actualNumber = nextFlag;
                this.notifyAll();
            }
        }
    }
}

class LockAndUnLock extends ReentrantLock {

    private int loop;

    public LockAndUnLock(int loop){
        this.loop = loop;
    }
    public void print(String str , Condition curr, Condition next){
        for (int i = 0; i < loop; i++) {
            this.lock();
            try{
                curr.await();
                System.out.print(str);
                next.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.unlock();
            }
        }
    }

}

class PackAndUnPack{

    private int loop;

    public PackAndUnPack(int loop){
        this.loop = loop;
    }

    public void print(String str , Thread next){
        for (int i = 0; i < loop; i++) {
            LockSupport.park();
            System.out.print(str);
            LockSupport.unpark(next);
        }
    }
}