package com.xx.juc.pc;

public class Test21 {
    public static void main(String[] args) throws InterruptedException {
        MessageQueue queue = new MessageQueue(2);
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            new Thread(() -> {
                queue.put(new Message(finalI,"第"+ finalI+"条数据"));
            },"生产者" + i).start();
        }

        Thread.sleep(1000);
        new Thread(() -> {
            while (true){
                Message take = queue.take();
                if (take == null) {
                    break;
                }
                System.out.println(take.getId() + "," + take.getValue());
            }

        },"消费者").start();

    }
}
