package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

import static java.lang.Thread.sleep;

@Slf4j
public class Test6 {
    public static void main(String[] args) throws InterruptedException {
        log.debug("main start");

        String prev = ref.getReference();
        int stamp = ref.getStamp();
        log.debug("{}",stamp);
        other();
        sleep(1000);
        log.debug("{}",stamp);
        log.debug("change A-> C {}",ref.compareAndSet(prev,"C",stamp,stamp +1));
    }

    private static void other() throws InterruptedException {
        new Thread( () -> {
            int stamp = ref.getStamp();
            log.debug("{}",stamp);
            log.debug("change A -> B {}",ref.compareAndSet(ref.getReference(),"B",stamp,stamp +1));
        },"t1").start();
        sleep(500);
        new Thread( () -> {
            int stamp = ref.getStamp();
            log.debug("{}",stamp);
            log.debug("change B -> A {}",ref.compareAndSet(ref.getReference(),"A",stamp,stamp +1));
        },"t2").start();
    }


    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A",0);

}
