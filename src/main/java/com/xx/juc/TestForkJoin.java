package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class TestForkJoin {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        Integer invoke = pool.invoke(new MyTask(1000));
        System.out.println(invoke);
    }
}


@Slf4j
class MyTask extends RecursiveTask<Integer>{

    private int n;

    public MyTask(int n){
        this.n = n;
    }
    @Override
    protected Integer compute() {
        if (n == 1){
            log.debug("{}",n);
            return 1;
        }
        MyTask myTask = new MyTask(n - 1);
        myTask.fork(); //让一个线程进行
        log.debug("{}",myTask);
        int result = n + myTask.join();
        return result;
    }
}