package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestSync {
    public static void main(String[] args) throws InterruptedException {
        ThreadUnsafe threadUnsafe = new ThreadUnsafe();
        for (int i = 0; i < 2; i++) {
            new Thread(() ->{
                threadUnsafe.method();
            },"t" + i).start();
        }
    }
}


class Room{
    int count = 0;

    public  void add(){
        count ++;
    }

    public  void sub(){
        synchronized(Room.class){
            count --;
        }
    }

    public synchronized int getCount() {
        return count;
    }
}


class ThreadUnsafe{
    List<Integer> list = new ArrayList<Integer>();

    public final void method(){
        for (int i = 0; i < 100; i++) {
            method2(list);
            method1(list);
        }
    }

    public void method2(List<Integer> list) {
        list.add(1);
    }

    public void method1(List<Integer> list) {
        list.remove(0);
    }
}
