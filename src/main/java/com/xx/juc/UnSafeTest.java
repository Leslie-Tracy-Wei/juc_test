package com.xx.juc;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

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