package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TestPool {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(2, 1000, TimeUnit.MILLISECONDS, 10,(queue,task) ->{new RuntimeException("aaaa");});
        for (int i = 0; i < 15; i++) {
            int j = i;
            threadPool.execute(() ->{
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}",j);
            });
        }
    }
}

@Slf4j
class ThreadPool{
    private BlockingQueue<Runnable> taskQueue;

    private HashSet<Worker> workers = new HashSet();
    private int coreSize;
    private long timeout;
    private TimeUnit timeUnit;
    private RejectPolicy<Runnable> rejectPolicy;

    public void execute(Runnable task){
        // 当任务没有超过coreSize 直接交给workder执行
        // 否则加入到队列中等待
        synchronized (workers){
            if (workers.size() < coreSize){
                Worker worker = new Worker(task);
                log.debug("新增worker {},{}，",worker,task);
                workers.add(worker);
                worker.start();
            } else{
//                taskQueue.put(task);
                // 1.死等 2带超时等待 3,放弃任务执行 4抛出异常 5让调用者自己执行任务
                taskQueue.tryPut(rejectPolicy,task);
            }
        }

    }
    public ThreadPool(int coreSize,long timeout,TimeUnit timeUnit,int queueCapcity,RejectPolicy<Runnable> rejectPolicy){
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapcity);
        this.rejectPolicy = rejectPolicy;
    }

    class Worker extends Thread{

        private Runnable task;
        public Worker(Runnable task){
            this.task = task;
        }
        @Override
        public void run() {
            // 执行任务
            // 当task不为空，执行任务
            // 当task执行完毕 接着从任务队列获取任务并执行
            log.debug("正在执行... {},",task);
            // task != null || (task = taskQueue.take()) != null 不带超时的
            while (task != null || (task = taskQueue.poll(timeout,timeUnit)) != null){
                try {
                    task.run();
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            synchronized (workers){
                log.debug("worker被移除 {},",this);
                workers.remove(this);
            }
        }
    }
}

@FunctionalInterface // 拒绝策略
interface RejectPolicy<T>{
    void reject(BlockingQueue<T> queue,T task);
}
@Slf4j
class BlockingQueue<T>{

    private Deque<T> queue = new ArrayDeque<>();

    private ReentrantLock lock = new ReentrantLock();

    // 生产者条件变量
    private Condition fullWaitSet = lock.newCondition();
    // 消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();

    private int capcity;

    // 带超时的阻塞获取
    public T poll(Long timeout, TimeUnit unit){
        lock.lock();
        try{
            //将超时时间转化为nanos秒
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()){
                try {
                    // 返回的是剩余的时间
                    if (nanos <= 0){
                        return null;
                    }
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }
    //  阻塞获取
    public T take(){
        lock.lock();
        try{
            while (queue.isEmpty()){
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    public void put(T task){
        lock.lock();
        try{
            while (queue.size() == capcity){
                try {
                    log.debug("等待加入任务队列 {},", task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {},", task);
            queue.addLast(task);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    // 带超时时间
    public boolean offer(T task,long timeout,TimeUnit timeUnit){
        lock.lock();
        try{
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capcity){
                try {
                    log.debug("等待加入任务队列 {},", task);
                    if (nanos <= 0){
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {},", task);
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public int size(){
        lock.lock();
        try{
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public BlockingQueue(int capcity){
        this.capcity = capcity;
    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            // 判断队列是否已满
            if (queue.size() == capcity) {
                rejectPolicy.reject(this,task);
            } else {
                log.debug("加入队列 {}",task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}