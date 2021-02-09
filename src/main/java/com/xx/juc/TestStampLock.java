package com.xx.juc;

import java.util.concurrent.locks.StampedLock;

import static java.lang.Thread.sleep;

public class TestStampLock {
}


class StampLock{
    private final StampedLock lock = new StampedLock();
    private Integer data;

    public void setData(Integer data) {
        this.data = data;
    }

    public int read(Long timeout) throws InterruptedException {
        long stamp = lock.tryOptimisticRead();
        sleep(timeout);
        if (lock.validate(stamp)){
            return data;
        }

        try{
            stamp = lock.readLock();
            sleep(timeout);
            return data;
        } finally {
            lock.unlockRead(stamp);
        }
    }
    public void write(int data){
        long stamp = lock.writeLock();
        try {
            sleep(2);
            setData(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}