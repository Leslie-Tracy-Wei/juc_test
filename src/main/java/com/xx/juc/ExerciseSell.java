package com.xx.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class ExerciseSell {
    public static void main(String[] args) throws InterruptedException {
//        TicketWindow window = new TicketWindow(10);
//        List<Thread> threads = new ArrayList<>();
//        for (int i = 0; i < 20000; i++) {
//            Thread t = new Thread(() ->{
//                boolean sell = window.sell(1);
//                if (sell){
//                    log.debug("success");
//                }
//                try {
//                    Thread.sleep(28);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });
//            t.start();
//            threads.add(t);
//        }
//        for (Thread thread : threads) {
//            thread.join();
//        }
//        log.debug("余票 >" + window.getCount());

        Account a = new Account(1000);
        Account b = new Account(1000);

        Thread t1 = new Thread(() ->{
            for (int i = 0; i < 1000; i++) {
                a.transfer(b,randomAmount());
            }
        },"t1");

        Thread t2 = new Thread(() ->{
            for (int i = 0; i < 1000; i++) {
                b.transfer(a,randomAmount());
            }
        },"t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.debug("a:{}",(a.getAmount()+b.getAmount()));
    }
    static Random random = new Random();
    public static int randomAmount(){
        return random.nextInt(100) + 1;
    }

}

class TicketWindow{
    private int count;

    public TicketWindow(int count){
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public boolean sell(int amount){
        if (this.count >= amount){
            this.count -= amount;
            return true;
        }
        return false;
    }
}


class Account{
    private int amount;

    public Account(int amount){
        this.amount = amount;
    }


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void transfer(Account next,int money){
        synchronized (Account.class){
            if (this.amount >= money) {
                this.setAmount(this.getAmount() - money);
                next.setAmount(next.getAmount() + money);
            }
        }

    }
}
