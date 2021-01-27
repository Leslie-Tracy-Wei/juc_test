package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@Slf4j
public class TestCurrThread {
    static Random random = new Random();
    static List<String> foods = new ArrayList<>();
    static {
        foods.add("水煮鱼");
        foods.add("大闸蟹");
        foods.add("宫保鸡丁");
    }
    public static void main(String[] args) {



        ExecutorService waiter = Executors.newFixedThreadPool(1);
        ExecutorService foodmaker = Executors.newFixedThreadPool(1);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            log.debug("run..");
//        },1,1,TimeUnit.SECONDS);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.withHour(18).withMinute(0).withSecond(0).withNano(0).with(DayOfWeek.THURSDAY);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            log.debug("run..");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },1,1,TimeUnit.SECONDS);
        waiter.execute(() ->{
            log.debug("客人来了，开始订餐");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("客人点完单，准备给后厨");
            Future<String> submit = foodmaker.submit(() -> {
               return makeFood();
            });
            try {
                log.debug("客人的菜 -》 " + submit.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        waiter.execute(() ->{
            log.debug("客人来了，开始订餐");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("客人点完单，准备给后厨");
            Future<String> submit = foodmaker.submit(() -> {
                return makeFood();
            });
            try {
                log.debug("客人的菜 -》 " + submit.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    private static String makeFood() throws InterruptedException {
        Thread.sleep(100);
        return foods.get(random.nextInt(3));
    }
}
