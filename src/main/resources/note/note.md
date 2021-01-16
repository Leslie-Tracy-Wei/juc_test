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
        NEW: 同初始状态 还没与操作系统关联起来
        RUNNABLE: 与操作系统关联起来 包含初始状态和可运行状态和阻塞状态 能与BLocked waiting time_waiting互相转换
        BLOCKED: 
        WAITING: join wait、notify 
            竞争锁成功 回runnable 否则到BLOCKED
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
    i ++ : 
        getstatic i // 获取静态变量的值
        iconst_1 // 准备常量1
        iadd     // 自增
        putstatic // 将修改后的值存入静态变量i
    临界区Critical Section
        多个线程访问共享资源时，读写发生了指令交错
    竞态条件Race Condition
        多个线程在临界区内执行，由于代码的执行顺序不同而导致结果无法预测
    
    解决临界区的方案：
        synchronized:阻塞式 对象锁 同一时刻一个线程只能持有对象锁
        实际是用对象锁保证了临界区内代码的原子性 ，代码是执行是不可分割的;
        要持有同一把对象锁;
        原子性跟锁对象~~~
        
        方法上增加synchronize 等价于锁住当前对象 锁的都是对象
        加在静态方法上，相当于锁当前类对象
    线程八锁:
    
    
    成员变量和静态变量
        如果他们没有共享，则线程安全
        如果他们被共享了，要根据他们的状态是否能够改变，分两种情况:
            如果只有读操作，则线程安全
            如果有读写操作，则代码是在临界区，要考虑线程安全
    局部变量
        局部变量是安全的 因为每个局部变量都存在于栈帧中，都是私有的
        但是局部变量应用的对象则未必
            如果该对象没有逃离方法的作用范围，则是线程安全的
            如果逃离了方法的作用范围，则是非安全的    ---逃逸分析
        方法的修饰符 private final 就隐含了线程私有的概念了
     
     
     线程安全:多个实例调用他们同一个实例的某个方法时，是线程安全的
     多个方法组合使用就不一定了 单个方法是原子的，但是组合就不是了，因为当前方法执行完后，就释放锁了
     
     不可变类线程安全性: String Integer ...
     
     final Date d2 = new Date():也是线程不安全的，只是引用是final 但是引用中的属性是可能被改动的
     
     对于多个对象实例 锁class
     对于单个 锁对象

#### Monitor 
    Java对象头:
        Mark Word (32bits):
            hashcode 25 age 4(垃圾回收分代年龄) biased_lock:0(偏向锁) 01(锁状态)
        Klass Word(32Bits):
            存放类对象信息
        array length: 数组对象有 
    Integer : 占 8 + 4
    int : 4
    
    Monitor 监视器、管程
    每个java对象关联一个monitor对象 
        monitor包含
            waitSet : 用于wait、notify 
            entryList : 等待队列 让线程进入blocked状态
            Owner : 持有者
    当线程调用同步代码时，就会尝试用markword去指向monitor对象

#### synchronized原理
    monitorenter: 将lock对象markword置为monitor指针
    monitorexit: 将lock对象markword重置，唤醒EntryList
    
    monitor:
    轻量级锁: 如果加锁的时间是错开的，即无竞争的情况下 语法还是synchronized 优先使用轻量级锁
            缺点:
                每次重入仍然需要执行CAS操作
    在栈帧中生成一个锁记录lock record 包含地址00 Object Refenrece 
    00 轻量级锁 10重量级锁
    synchronized 锁重入 Lock record中地址为null 
    偏向锁:
        Java 6 优化，只要第一次将markWord替换为ThreadId，后面只要发现还是自己的锁，那么就不进行cas
        biased_lock:0(偏向锁) 表示偏向锁的状态 1为启用 默认是开启的 对象头为101
        偏向锁不会在对象生成马上生成 需要一定时间生成
        获取对象的hashcode() 会使偏向锁失效。
    锁膨胀:
        在尝试加轻量级锁的过程中，CAS操作无法成功，说明其他线程给对象加入了轻量级锁(有竞争)，这时候就需要锁膨胀，将轻量级锁升级为重量级锁
    
    自旋优化:
        如果当前线程自旋成功，即结束了同步代码部分，释放了锁，说明当前线程可以避免阻塞 避免上下文切换
        jvm底层自己控制自旋次数
    
    wait/notify 也会撤销偏向锁，轻量级锁
    
    批量重偏向:
        当频繁获取对象锁时 当阈值超过20 就重新偏向到新的线程id
    批量撤销:
        当撤销偏向锁阈值操作40次后，jvm会认定根本不该偏向，于是整个类的对象都会变为不可偏向，新建的对象也不可偏向
    
    锁消除:
        JIT 即时编译器 热点代码 进行优化 
        可以通过-XX:-EliminateLocks 开启关闭


#### wait、notify
    原理:
        进入waitSet 变为WAITING状态 
        WAITING和BLOCKED都是阻塞 不占用cpu
        BLOCKED线程会在OWNER线程释放锁唤醒
        WAITING线程会在Owner线程调用notify、notifyAll唤醒，进入到waitSet，仍需进入EntrySet重新竞争
        必须是获得对象的锁才能调用wait notify
        wait(0) 相当于无限制的等待
        wait(long timeOut) 有时限的等待

#### wait sleep
    sleep是Thread的静态方法 wait是对象的方法 
    sleep不需要强制配合synchronized使用，但wait一定要搭配synchronized使用
    sleep在睡眠的同时，不会释放锁，wait会释放锁
    
    状态都是一样的 time_waiting

#### 同步模式 保护性暂停 
    一个线程等另一个线程的结果
    只能是1对1
    解耦 产生结果的线程 和接收结果的咸亨

#### join 原理
    底层是使用wait()来实现的，使用保护性暂停

#### 异步模式 生产者、消费者模式
    生产者只生产数据 ，消费者只处理数据
    一般使用一个队列来传递消息

#### park / unpark
    park:暂停当前线程 对应状态为wait
    unpark:恢复当前线程 可以在park前调用 也可以在后调用
    
    与wait/notify比较:
        wait/notify必须与Object monitor配合使用 ，而unpark不需要
        park&unpark是以线程未单位进行阻塞和唤醒的，而notify是随机一个，notifyAll是全部，因此不是那么精确
        park&unpark没有顺序要求，而notify一定是要在wait之后
    原理:
        每个线程都有一个park对象 : _counter _cond _mutex
        调用park就是看需不需要停下来 进_cond休息
        调用unpark就是让_counter增加 多次调用_counter只会增加一次


#### 多把锁 
    增加并发度
    但是容易产生死锁
#### 线程活跃性
    死锁:
```java
 public static void main(String[] args) {

        Object a = new Object();
        Object b = new Object();
        new Thread(() ->{
            synchronized (a){
                log.debug("持有a");
                try {
                    Thread.sleep(11);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (b){
                    log.debug("持有b");
                }
            }
        }).start();
        new Thread(() ->{
            synchronized (b){
                log.debug("持有b");
                try {
                    Thread.sleep(11);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (a){
                    log.debug("持有a");
                }
            }
        }).start();


    }
```

    定位死锁
        jps 查看所有id 
        jstack 查看线程状态
        jconsole定位死锁 -》 检测死锁
        
    哲学家就餐问题:
        要么思考 要么吃饭
    
    活锁:
        两个线程因为结束条件不一样，导致两个线程一直执行 
    饥饿:
        由于线程的优先级太低，始终得不到CPU的调度
        顺序加锁: 可以解决死锁，但是会导致线程饥饿情况

##### ReentrantLock
    与synchronized比较:
        synchronized不能中断 
        可以设置超时时间 tryLock 尝试获取锁 tryLock(long time,TimeUnit)
        可以设置为公平锁 防止线程饥饿 一般不设置公平锁 因为会降低并发度
        支持多个条件变量
            synchronized 中waitSet就是条件变量
            ReentrantLock 支持多个休息室 那么就可以当个去单个指定唤醒
            
            使用流程:
                await前需要获得锁
                await 执行后，会释放锁 进入conditionObject等待
                await的线程被唤醒(或者打断，或者超时) 重新竞争lock锁
                竞争lock锁成功后，从await后继续执行
        都支持可重入
    
    可重入 指的是同一个线程如果首次获得了这把锁，那么因为他是这把锁的主人，因此有权利再次获取这把锁
    如果是不可重入，那么第二次获得锁的时候，自己也会被挡住
    
    
#### 同步模式 顺序控制
    wait notify实现方式:
        
    
    