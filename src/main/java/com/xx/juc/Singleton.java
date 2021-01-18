package com.xx.juc;

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
