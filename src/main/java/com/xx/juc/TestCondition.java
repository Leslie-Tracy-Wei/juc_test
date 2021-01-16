package com.xx.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TestCondition {
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Condition con = lock.newCondition();
        Condition con2 = lock.newCondition();

    }
}
