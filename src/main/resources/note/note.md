## 并发编程

#### 进程与线程
##### 进程
    程序由指令和数据组成，进程就是用来加载指令，管理内存，管理io的
    程序可以看做是一个程序的一个实例

##### 线程
    一个进程之内可以分为多个线程
    一个线程就是一个指令流 
##### 比较
    进程基本上是互相独立的，而线程存在于进程内，是进程的一个子集
    进程拥有共享的资源，如内存空间等，供其内部的线程共享
    进程的通讯较为复杂
        同一台机器的进程通讯称为IPC (inter-process communication)
        不同计算器之间的通讯 需要通过网络，并遵守共同的协议 如http
    线程的通讯相对简单，因为它们共享进程的内存，多个线程可以访问同一个共享变量
    线程比较轻量，线程上下文切换成本比进程上下文切换要低

#### 并行与并发
##### 并发
    **同一时间应对( dealing with)多件事情的能力**
    单核cpu下，线程其实还是串行执行的，但是操作系统有个任务调度器，分配cpu的时间片
    只是切换时间很多，人类很难感知到， 因此微观串行，宏观并行
    时间片轮转 就称为并发 concurrent
##### 并行
    **同一时间做(doing)多件事情的能力**
    真正同时运行 多核cpu下 parallel


#### 异步与同步
    都是从方法调用者来讲的:
        如果需要等待结果，就是同步
        如果不需要等待结果，就是异步 多线程可以将方法执行变为异步
        
#### jmh 工具
    多线程在多核cpu下提高效率，但在单核cpu下并无太大的区别


#### 创建线程
    1.使用Thread  
    2.使用Runnable 推荐
    3.使用lambda表达式
    4.FutureTask  JUC 增加返回值
    
#### 查看进程，线程
    linux
        ps -fe 查看所有线程
        ps -fT -p <PID> 查看某个进程的所有
        kill pid 杀死进程
        top 查看进程信息
        
    java
        jps 查看所有java进程
        jstack pid 
        jconsole 图形化查看
#### 线程运行原理
    栈与栈帧
        线程启动后，jvm都会分配一块栈内存，栈又有栈帧组成
        栈帧为每次方法调用时占用的内存
        栈帧包含:**局部变量表，操作数栈，动态链接，方法返回值**和一些附带信息
        栈帧执行完后，就销毁了，内存就释放了
        
#### 线程上下文切换
    使用CPU -> 不使用CPU
    1.线程的CPU时间片用完
    2.垃圾回收
    3.有更高优先级的线程需要执行
    4.线程自己调用sleep yield wait等方法
    
    程序计数器，作用保存当前线程的状态 是记住下一条jvm指令的执行地址 线程私有的
    频繁的上下文切换，会影响性能
#### 线程中常用方法
    start(): 让线程进入就绪状态，由线程调度器决定是否运行，线程start()只能调用一次
    run(): 新线程调用的时候，会自动调用
    join()、join(long n ): 等待线程运行结束 n最长等待时间
    sleep(): 不会释放锁 
    yield(): 让出当前线程
    ...

#### run 与 start 区别
    run() 只是一个普通方法；
    start() private native void start0()
    
#### sleep() 与 yield() 区别

    sleep: RUNNABLE -> TIME WAITING(阻塞)
    其他线程可以使用interrupt来打断 java.lang.InterruptedException: sleep interrupted
    睡眠结束后的线程，未必立即得到执行
    使用TimeUnit的sleep代替Thread.sleep()
    
    yield: RUNNABLE -> RUNNABLE就绪 然后调度执行其他线程
    具体的实现依赖于操作系统的任务调度器
    
    就绪状态会获得时间片，而阻塞状态不会
#### 线程优先级
    PRIORITY: 1 - 10
    让调度器优先调用该线程 但是不一定优先
    当cpu比较忙，那么优先级高的线程会获得更多的时间片，空闲时，几乎没用
#### 线程状态
    NEW : 新建线程
    RUNNABLE: 运行
    
#### 案例 防止CPU占用100%
    利用while(true)中加入sleep 或者yield


#### join的使用
    等待线程执行结束
    多个join是并行执行的 所以 耗时取长的那个 
    join(long) 不一定要等待long 只要线程结束了就返回
   
#### interrupt
    打断处于阻塞线程 sleep wait join isInterrupt为false 直接抛出异常
    打断正常线程 isInterrupt为true 被打断的线程不会停下来，需要判断isInterrupt 不会清楚标记
    interrupted() 静态方法 会清除标记
    
    LockSupport : 打断表示为true 会让park失效
    

#### 多线程的设计模式
    两阶段终止模式: 在一个线程t1中优雅的结束线程t2
    错误使用:
        使用stop() 因为stop会将线程直接杀死 但是有可能该线程有资源共享的部分锁住了，这样就没办法释放锁
    
``` java
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

```

#### 不推荐使用的方法
    stop():
    suspend():
    resume(): 容易破坏同步代码块，影响锁的使用
    
#### 主线程、守护线程
    只要还有线程还在运行 ，java进程就还在运行
    只要别的线程结束了，那么守护线程就会被强制结束
    setdaemon(true) 设置守护线程
    例子:
        垃圾回收器就是个守护线程
        Tomcat：accpetor和poller也是守护线程


#### 线程状态
    从操作系统:
        初始状态 -> 可运行状态 -> 运行状态 -> 阻塞状态 -> 终止状态 
        初始状态: 仅仅只是创建了线程
        可运行状态: 指已经创建了，可以让cpu调用
        运行状态: 获取到cpu时间片运行的状态 切换 -》 上下文切换会导致
        阻塞状态: 只要一直不被唤醒，调度器就不会考虑调度
        终止状态: 生命周期结束
     从语言层面:
        NEW: 同初始状态
        RUNNABLE: 包含初始状态和可运行状态和阻塞状态
        
        BLOCKED:
        WAITING: join
        TIME_WAITING: sleep
        
        TERMINATED: 生命周期结束
        
#### 线程状态
```java
@Slf4j
public class TestThreadStatus {
    public static void main(String[] args) throws InterruptedException {

        // new
        Thread t1 = new Thread("t1"){
            @Override
            public void run() {
                log.info("aaaa");
            }
        };


        // RUNNABLE
        final Thread t2 = new Thread("t2"){
            @Override
            public void run() {
                while (true){

                }
            }
        };
        t2.start();

        // TIME_WAITING
        Thread t3 = new Thread("t3"){
            @Override
            public void run() {
                synchronized (TestThreadStatus.class){
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t3.start();


        // WAITING
        Thread t4 = new Thread("t4"){
            @Override
            public void run() {
                try {
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t4.start();

        // BLOCKED
        Thread t5 = new Thread("t5"){
            @Override
            public void run() {
                synchronized (TestThreadStatus.class){
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t5.start();

        // TERMINATED
        Thread t6 = new Thread("t6"){
            @Override
            public void run() {

            }
        };
        t6.start();


        Thread.sleep(500);
        log.debug(" t1 -》 " + t1.getState());
        log.debug(" t2 -》 " + t2.getState());
        log.debug(" t3 -》 " + t3.getState());
        log.debug(" t4 -》 " + t4.getState());
        log.debug(" t5 -》 " + t5.getState());
        log.debug(" t6 -》 " + t6.getState());
    }
}
```


#### 共享模型
    管程-悲观锁(阻塞):
        