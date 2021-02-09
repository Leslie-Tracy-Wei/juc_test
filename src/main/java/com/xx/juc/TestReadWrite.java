package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestReadWrite {
}


@Slf4j
class DataContainer{

    private Object data;
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock r = rw.readLock();
    private ReentrantReadWriteLock.WriteLock w = rw.writeLock();



    public void read(){
        try {
            r.lock();
            log.debug("开始读数据");

        }finally {
            r.unlock();
        }

    }

    public void write(){
        try {
            w.lock();
            log.debug("写数据");

        }finally {
            r.unlock();
        }

    }
}