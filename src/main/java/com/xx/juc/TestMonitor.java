package com.xx.juc;

/**
 * 模拟监控线程
 */
public class TestMonitor {
    public static void main(String[] args) throws InterruptedException {
        TwoParseTermination t = new TwoParseTermination();
        t.start();

        Thread.sleep(5000);

        t.stop();
    }
}



class TwoParseTermination{
    private Thread monitor;

    public void start(){
        monitor = new Thread("monitor"){
            @Override
            public void run() {
                while (true) {
                    Thread curr = Thread.currentThread();
                    if (curr.isInterrupted()){
                        System.out.println("处理后事");
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                        System.out.println("处理监控");
                    } catch (InterruptedException e) {
                        curr.interrupt();
                    }

                }
            }
        };

        monitor.start();
    }

    public void stop(){
        monitor.interrupt();
    }
}
