package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestDrinkTea {
    public static void main(String[] args) throws InterruptedException {
        final Thread water = new Thread("water"){
            @Override
            public void run() {
                try {
                    log.debug("洗水壶");
                    TimeUnit.SECONDS.sleep(1);
                    log.debug("烧开水");
                    TimeUnit.SECONDS.sleep(10);
                    log.debug("开水已经开了");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        Thread wash = new Thread("wash"){
            @Override
            public void run() {
                try {
                    log.debug("洗茶壶");
                    TimeUnit.SECONDS.sleep(1);
                    log.debug("洗茶杯");
                    TimeUnit.SECONDS.sleep(1);
                    log.debug("拿茶叶");
                    TimeUnit.SECONDS.sleep(1);
                    water.join();
                    log.debug("泡茶喝茶");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        water.start();
        wash.start();

    }
}
