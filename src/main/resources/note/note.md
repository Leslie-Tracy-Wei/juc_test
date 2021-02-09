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
    1.使用interrupt 
    2.volatile：
        
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
      
#### 共享模型
    内存：
        主存 所有线程共享数据
        工作内存 每个线程独立使用的数据 
    JMM
        原子性：保证指令不会受到线程上下文切换的影响
        可见性：保证指令不会受到CPU缓存的影响 
            造成的原因是JIT编译器会将run的值缓存到自己的工作内存中的高速缓存，减少对主存run的访问，提高效率
            增加volatile会读主存
                volatile用来修饰修饰成员变量和静态成员变量 让程序强制读取主存的值 只能保证可见性 不能解决指令交错
                synchronized能够保证原子性和可见性 但是性能比volatile低
        有序性： 增加volatile保证指令不会受到cpu指令并行优化的影响，指令重排序
            指令五阶段 取指令 - 指令译码 - 执行指令 - 内存访问 -数据写回
                        IF - ID      - EX     - MEM     - WB
            多级指令流水线 这样能够提高指令的吞吐率
            分阶段，分工是提高效率的关键 重排指令不能影响结果
            
            
        volatile:
            原理  
                仅仅只能保证之后的读能够读到最新的结果，不能保证读跑到他前面去 有序性
                只能保证本线程相关代码不被重排序  可见性
                
                内存屏障 memory barrier
                对volatile变量的写指令后会加入写屏障：对共享变量的改动，都会同步到主存中
                防止写屏障的代码排在写屏障之后
                num = 2;
                ready = true ; // ready是volatile带来写屏障
                // 写屏障
                ....
                
                对volatile变量的读指令前会加入读屏障,b保证读取共享变量是从主存中来
                防止读屏障之后的代码出现在读屏障之前
                ....
                // 读屏障
                if (ready){
                    x = num +num;
                } else{
                    x = 0;
                }
#### Balking模式
    犹豫模式: 避免执行多次 ，保证只执行一次
    线程安全的单例 监控线程
    
#### 双重检查锁问题
```java
// double checked locking
// 懒汉式
// 只有第一个if使用到INSTANCE
public class Singleton {
    private Singleton(){

    }
     public static volatile Singleton INSTANCE = null;

    public static Singleton getInstance(){
        // new Singleton -> 引用地址 -> *调用构造函数 -> *赋值给变量
        // 可能先赋值给变量 再调用构造函数 ，可能还没构造函数，但是INSTANCE已经有
        // synchronized不能阻止重排序
        if (INSTANCE == null){ // 主要是这一步没有完全被synchronized保护
            synchronized (Singleton.class){
                if (INSTANCE == null){
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }
}
```

#### happens-before
    规定了对共享变量的写操作对其他线程的读操作可见，他是可见性与有序性的一套规则总结



#### 无锁并发 乐观锁 cas
    cas compareAndSet必须是原子操作的
    cas底层实际是lock cmpxchg 在单核cpu和多核cpu下能够保证原子性
```java
    public void withDraw(Integer amount){
    while(true){
        int prev = balance.get();
        int next = prev - amount;
        if (balance.compareAndSet(prev,next)){
            break;
        }   
    }
}
```    
    cas与volatile cas只能保证原子性，所以value都是被volatile修饰 保证其有序性和可见性
    为什么无锁效率高:
        因为线程并没有发生上下文切换，而synchronized会发生上下文切换(成本大)
        CAS适合于线程数少，多核CPU的场景下
    CAS是基于乐观锁，实际无锁的思想，不怕别的线程修改共享变量，就算改了，可以重试；
    但是竞争激烈的话，一直的循环尝试反而会影响效率
    synchronized基于悲观锁，防止别人来修改，直接上锁    

#### 原子整数
    AtomicBoolean
    AtomicInteger
    AtomicLong
    
    
#### 原子数组 
    保护数组内的安全
    AtomicIntegerArray
    AtomicLongArray
    AtomicReferenceArray
#### 原子引用
    AtomicReference
    AtomicMarkableReference ： 关心是否被更改过
    AtomicStampedReference  ： 解决ABA问题，使用版本号

#### 字段更新器
    AtomicIntegerFieldUpdater   
    AtomicLongFieldUpdater
    AtomicReferenceFieldUpdater
    
#### 原子累加器
    效率比Atomic要高，就是在有竞争的情况下 设置多个累加单元 Thread-0累加Cell[0] ....Thread-n累加Cell[n] ，最终汇总结果，操作在不同的cell变量，减少CAS重试失败，提高性能
    LongAdder
    DoubleAdder
    源码:
        cells[] : 累加单元数组，惰性初始化
        base: 基础值，无竞争时，累加该值
        cellsBusy: 在Cells创建时或者扩容时，置为1，表示加锁
        cpu -> memory 耗费时间长 120-240cycle 
        cpu -> L1,L2,L3 速度快一点
        @sun.misc.Contended 防止缓存行伪共享 ，原理是防止缓存行存在多个cells
        缓存行:每一个缓存行对应一块内存 
        伪共享: 
#### CAS ABA问题


#### 函数式接口
    supplier : 提供者 特点是无中生有 () -> 结果
    function : 函数 特点一个参数一个结果  (参数params) -> result, BiFuntion (参数1，参数2 ) -》 result
    consumer : 消费者 特点 一个参数没有结果 (参数) -> void ,BiFuntion(参数1，参数2) -> void
```java
public class Test7 {

    public static void main(String[] args) {
        demo(
                () -> new int[10],
                (array) -> array.length,
                (array,index) -> array[index] ++,
                (array) -> System.out.println(Arrays.toString(array))
        );

        demo(
                () -> new AtomicIntegerArray(10),
                (array) -> array.length(),
                (array,index) -> array.getAndIncrement(index),
                (array) -> System.out.println(array)
        );
    }
    private static <T> void demo(
            Supplier<T> arraySupplier,
            Function<T,Integer> lengthFun,
            BiConsumer<T,Integer> putConsumer,
            Consumer<T> printConsumer
    ){
        List<Thread> ts = new ArrayList<>();
        T array = arraySupplier.get();

        int length = lengthFun.apply(array);

        for (int i = 0; i < length; i++) {
            ts.add(new Thread(() ->{
                for (int j = 0; j < 10000; j++) {
                    putConsumer.accept(array,j % length);
                }
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {

                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        printConsumer.accept(array);
    }
}
```

#### Unsafe
    底层提供操作内存，线程的方法
#### 手写一个AtomicInteger
```java
public class UnSafeTest {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InterruptedException {

//        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
//        theUnsafe.setAccessible(true);
//
//        Unsafe unsafe = (Unsafe)theUnsafe.get(null);
//
//        System.out.println(unsafe);
//
//        long idOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
//        long nameOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));
//        Teacher teacher = new Teacher();
//        System.out.println(teacher);
//
//        unsafe.compareAndSwapInt(teacher,idOffset,0,1);
//        unsafe.compareAndSwapObject(teacher,nameOffset,null,"tom");
//
//
//        System.out.println(teacher);
        MyAtomicInteger myAtomicInteger = new MyAtomicInteger(10000);
        for (int i = 0; i < 1000; i++) {
            new Thread(() ->{
                myAtomicInteger.decrement(10);
            }).start();
        }

        Thread.sleep(4000);

        System.out.println(myAtomicInteger.getValue());
    }
}


class Teacher{

    volatile int id;
    volatile String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}



class MyAtomicInteger{

    public MyAtomicInteger(int value){
        this.value = value;
    }
    private volatile int value;
    private static final long valueOffset;
    static final Unsafe UNSAFE;

    static {
        Field theUnsafe = null;
        try {
            theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();


        }
        theUnsafe.setAccessible(true);

        try {
            UNSAFE = (Unsafe)theUnsafe.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        try {
            valueOffset = UNSAFE.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public int getValue(){
        return value;
    }


    public void decrement(int amount){
        for (;;){
            int prev = this.value;
            int next = prev - amount;
            if (UNSAFE.compareAndSwapInt(this,valueOffset,prev,next)) {
                break;
            }
        }
    }
}
```


#### 不可变类
    SimpleDateFormat 线程非安全的
    DateTimeFormatter 线程安全的
    
    final ：
        属性用final修饰保证了该属性是只读的，不可修改
        类用final修饰保证该类中的方法不能被覆盖，防止子类无意破坏不可变性
        String构造函数中用复制过来 保护性拷贝来保证不引用同一个 意思就是通过创建副本对象来避免共享的手段称为保护性拷贝
    
    
#### 享元模式 Flyweight pattern 
    当需要重用数量有限的同一类对象
    包装类中的valueOf()方法使用到享元模式 其实就是资源池重用

```java
 private static class LongCache {
        private LongCache(){}
        ~~~~
        // 一个缓存的数组
        static final Long cache[] = new Long[-(-128) + 127 + 1];
        // 初始化
        static {
            for(int i = 0; i < cache.length; i++)
                cache[i] = new Long(i - 128);
        }
    }
```    
    每个单个方法是原子的，但是组合操作并不是原子的

#### final原理
    设置final变量: final int a = 10; 会赋值后，在后面增加写屏障


#### 并发工具
    自定义线程池:
        线程池 + 阻塞队列  
    ThreadPoolExecutor:
        线程池状态:  
            使用int的高3位表示线程池状态 ，低29位表示线程数量 
            为了保证这些信息存储在一个原子变量ctl中，
            目的是将线程池状态与线程个数合二为一，这样就可以用一次cas原子操作进行赋值
            rs | wc  rs：run status wc: worker count
            RUNNING 111 Y Y 
            SHUTDOWN 000 N Y  不会接受新任务 但是会处理阻塞队列剩余任务
            STOP     001 N N  会中断正在执行的任务 并抛弃阻塞队列任务
            TIDYING  010 -- 任务全部执行完毕 活动线程位0 即将进入终止状态
            TERMINATED 011 -- 终结状态
            
            ThreadPoolExecutor(int corePoolSize // 核心线程数 (最多保留的核心数) 救急线程有生存时间,
                              int maximumPoolSize // 最大线程数,
                              long keepAliveTime // 生存时间 针对救济线程,
                              TimeUnit unit // 时间单位,
                              BlockingQueue<Runnable> workQueue // 阻塞队列,
                              ThreadFactory threadFactory // 线程工厂--可以为线程创建时起名字,
                              RejectedExecutionHandler handler) // 拒绝 策略
            只有队列选择了有界队列，那么任务超过了队列大小时，会创建 maximumPoolSize-  corePoolSize 数目的线程来救急
            jdk4种实现:   
                AbortPolicy让调用者抛出异常，默认策略
                CallerRunsPolicy让调用者运行任务
                DiscardPolicy放弃本次任务
                DiscardOldestPolicy放弃队列中最早的任务，本任务取而代之
                其他框架的增强或实现 dubbo netty pinpoint...
            
            Executors:
                newFixedThreadPool:没有救急线程 只有核心线程，阻塞队列是无界的 LinkedBlockingQueue，
                适用于任务量已知，相对耗时的
                newCacheThreadPool:全部都是救急线程 60秒后回收 SynchronousQueue 它没有容量 相当于来取才有放 一手交钱一手交货 ，
                适合任务密集的，执行时间短的
                newSingleThreadPool:只有一个核心线程，没有核心线程，阻塞队列是无界的 LinkedBlockingQueue 
                适用于任务需要串行的操作 普通的创建线程执行没有补救的机制，但是该线程池会补救，保证有一个可用的线程
                return new FinalizableDelegatedExecutorService
                            (new ThreadPoolExecutor(1, 1,
                                                    0L, TimeUnit.MILLISECONDS,
                                                    new LinkedBlockingQueue<Runnable>())); 返回的是一个包装后的对象 对外只暴露ExecutorService方法，不可变
            
                newScheduledThreadPool:带任务调度的线程池 ，任何一个任务出现异常不会影响其他的
            execute跟sumbit 接收的参数不一样 一个是Runnable 一个是callable有返回值
            invokeAll()提交所有的任务 带timeout的话就会直接放弃后续任务
            invokeAny()只要有一个任务完成，那么就会返回结果，取消后面的任务
            shutdown() 将状态转为shutdown 不会阻塞调用线程的执行
            shutdownNow() 状态转为stop 并会interrupt中断进行中的任务

#### 异步模式之工作线程
    让有限的工作线程来轮流异步处理无限多的任务
    

#### 线程池大小
    CPU密集型运算: 做数据分析 使用Cpu 实现CPU最优利用 大小 cpu核数 + 1
    I/O密集型运算: 执行业务计算时，经验公式: 线程数 = 核数 * 期望CPU利用率 * 总时间(CPU计算时间 + 等待时间) / CPU计算时间
    计算时间越短，线程数应该越多


#### fork/join 
    jdk1.7加入的，体现的是一种分治的实现，适用于能够将任务拆分的cpu密集型计算 -》 将大任务拆分成小的任务 直到不能继续拆分
    
#### JUC
    aqs:
        抽象队列式同步器:是阻塞式锁和其他同步器工具的框架
        state： 
            独占：只有一个线程可以访问资源
            共享：允许多个线程访问资源
        提供一个FIFO等待队列 类似于Monitor的entrySet
        条件变量来实现等待，唤醒 支持多个条件变量 类似于monitor的waitSet
        相关方法:
            tryAcquire():获取锁
            tryRelease():释放锁
    自定义锁:
```java

@Slf4j
public class TestAQS {
    public static void main(String[] args) {
        MyLock lock = new MyLock();
        new Thread(() ->{
            try{
                lock.lock();
                log.debug("lock.....");
                lock.lock();
                log.debug("lock.....");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                log.debug("unlock.....");
                lock.unlock();
            }

        }).start();

        new Thread(() ->{
            try{
                lock.lock();
                log.debug("lock.....");
            } finally {
                log.debug("unlock.....");
                lock.unlock();
            }

        }).start();
    }
}

class MyLock implements Lock{

    // 独占类 同步器类
    class MySync extends AbstractQueuedSynchronizer{
        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0,1)){
                // 成功加上锁 并设置owner为当前线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            // 循序有要求 保证加
            setState(0);
            return true;
        }

        @Override // 是否持有独占锁
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition(){
            return new ConditionObject();
        }
    }
    private  MySync mySync = new MySync();
    @Override
    public void lock() {
        mySync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        mySync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return mySync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return mySync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        mySync.release(1);

    }

    @Override
    public Condition newCondition() {
        return mySync.newCondition();
    }
}
```
    reentranLock原理:
        可重入原理:
            state++ 一直累加上去 保证解锁的时候 每次减1  state--减为0才解锁
            
```java
final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
```

#### 读写锁
    ReentrantReadWriteLock : 当读操作远远高于写操作时，这时候使用读写锁让读-读并发 提高性能
    读锁不能支持条件变量
    重入时升级不支持，持有读锁的情况下获取写锁，会导致写锁永久等待
    但是支持降级，就是持有写锁的情况下获取读锁， 
    可以应用到缓存中
    
    StampedLock:进一步优化读性能，在使用读，写的时候都使用戳来加解锁
        缺点:不支持条件变量 不支持重入
 
#### 信号量 semaphore 
    用来限制能同时访问共享资源的线程上限  并不能限制资源数
    用来做单机的限流


#### 倒计时锁 countDownLatch
    不能复用
    
#### 循环栅栏 cyclicBarrier
    升级版的countDownLatch

#### 线程安全集合类
    hashTable 
    Blocking大部分都是实现基于锁 并提供用来阻塞的方法
    CopyOnWrite 以拷贝的形式 修改的开销比较重
    Concurrent:
        性能比较高 使用cas和多把锁 一般提高提高吞吐量
        弱一致性 遍历的弱一致性 求集合大小的弱一致性 读取弱一致性
        遍历时如果发生修改，对于非安全的容器来讲，会使用fail-fast机制，让遍历立即失效
        抛出ConcurrentModificationException 
    
    ConcurrentHashMap:
        computeIfAbsent：如果缺少一个key，则计算生成一个value，然后将key value放入map 保证get ，put的原子性
        jdk7 hashmap： 后加入的会加在链表头 并发死链
        jdk8 hashmap： 扩容算法调整，不再将元素加入到链表头，但是在多线程下也不能正常扩容，还是会出现其他问题 如扩容丢数据