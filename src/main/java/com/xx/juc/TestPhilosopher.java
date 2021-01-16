package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TestPhilosopher {
    public static void main(String[] args) {

        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");
        new Philosopher(c1,c2,"aaa").start();
        new Philosopher(c2,c3,"bbb").start();
        new Philosopher(c3,c4,"ccc").start();
        new Philosopher(c4,c5,"ddd").start();
        new Philosopher(c5,c1,"fff").start();
    }
}

@Slf4j
class Philosopher extends Thread{
    Chopstick left;
    Chopstick right;
    String name;

    public Philosopher(Chopstick left,Chopstick right,String name){
        this.left = left;
        this.right = right;
        this.name = name;
    }
    @Override
    public void run() {
        while(true){
            if (left.tryLock()){
                try {
                    if (right.tryLock()){
                        try {
                            eat();
                        }finally {
                            right.unlock();
                        }
                    }
                }finally {
                    left.unlock();
                }
            }
        }
    }

    Random random = new Random();
    private void eat(){
        log.debug(this.name + "正在吃饭");
        try {
            TimeUnit.MILLISECONDS.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Chopstick extends ReentrantLock {
    String name;

    public Chopstick(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return "Chopstick{" +
                "name='" + name + '\'' +
                '}';
    }
}