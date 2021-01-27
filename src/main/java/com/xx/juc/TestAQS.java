package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Slf4j
public class TestAQS {
    public static void main(String[] args) {
        MyLock lock = new MyLock();
        new Thread(() ->{
            try{
                lock.lock();
                log.debug("lock.....");
                lock.lock();
                log.debug("lock.....");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                log.debug("unlock.....");
                lock.unlock();
            }

        }).start();

        new Thread(() ->{
            try{
                lock.lock();
                log.debug("lock.....");
            } finally {
                log.debug("unlock.....");
                lock.unlock();
            }

        }).start();
    }
}

class MyLock implements Lock{

    // 独占类 同步器类
    class MySync extends AbstractQueuedSynchronizer{
        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0,1)){
                // 成功加上锁 并设置owner为当前线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            // 循序有要求 保证加
            setState(0);
            return true;
        }

        @Override // 是否持有独占锁
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition(){
            return new ConditionObject();
        }
    }
    private  MySync mySync = new MySync();
    @Override
    public void lock() {
        mySync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        mySync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return mySync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return mySync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        mySync.release(1);

    }

    @Override
    public Condition newCondition() {
        return mySync.newCondition();
    }
}
