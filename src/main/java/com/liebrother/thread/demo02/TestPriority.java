package com.liebrother.thread.demo02;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class TestPriority {

//    public static void main(String[] args) {
//        MyThread myThread1 = new MyThread();
//        myThread1.setName("aaa");
//        myThread1.setPriority(Thread.MAX_PRIORITY);
//        MyThread myThread2 = new MyThread();
//        myThread2.setName("bbb");
//        myThread2.setPriority(Thread.MAX_PRIORITY - 1);
//        myThread1.start();
//        myThread2.start();
//
//    }

//    public static void main(String[] args) {
//        Thread.currentThread().setPriority(4);
//        Thread thread = new Thread();
//        System.out.println(thread.getPriority());
//    }

//    public static void main(String[] args) {
//        System.out.println(Thread.currentThread().getThreadGroup().getMaxPriority());
//        ThreadGroup threadGroup = new ThreadGroup("myThreadGroup");
//        threadGroup.setMaxPriority(5);
//        Thread[] threads = new Thread[10];
//        for (int i = 0; i < 10; i ++) {
//            threads[i] = new Thread(threadGroup, "myThread-" + i);
//            threads[i].setPriority(7);
//        }
//        for (int i = 0; i < 10; i ++) {
//            System.out.println(threads[i].getPriority());
//        }
//    }
//
//    static AtomicInteger index = new AtomicInteger(0);
//    static AtomicInteger minTimes = new AtomicInteger(0);
//    static AtomicInteger normTimes = new AtomicInteger(0);
//    static AtomicInteger maxTimes = new AtomicInteger(0);
//
//    public static void main(String[] args) {
//        List<MyThread> minThreadList = new ArrayList<>();
//        List<MyThread> normThreadList = new ArrayList<>();
//        List<MyThread> maxThreadList = new ArrayList<>();
//
//        int count = 1000;
//        for (int i = 0; i < count; i++) {
//            MyThread myThread = new MyThread("min----" + i);
//            myThread.setPriority(Thread.MIN_PRIORITY);
//            minThreadList.add(myThread);
//        }
//        for (int i = 0; i < count; i++) {
//            MyThread myThread = new MyThread("norm---" + i);
//            myThread.setPriority(Thread.NORM_PRIORITY);
//            normThreadList.add(myThread);
//        }
//        for (int i = 0; i < count; i++) {
//            MyThread myThread = new MyThread("max----" + i);
//            myThread.setPriority(Thread.MAX_PRIORITY);
//            maxThreadList.add(myThread);
//        }
//
//        for (int i = 0; i < count; i++) {
//            maxThreadList.get(i).start();
//            normThreadList.get(i).start();
//            minThreadList.get(i).start();
//        }
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("maxPriority 统计：" + maxTimes.get());
//        System.out.println("normPriority 统计：" + normTimes.get());
//        System.out.println("minPriority 统计：" + minTimes.get());
//
//    }
//
//    static class MyThread extends Thread {
//
//        public MyThread(String name) {
//            super(name);
//        }
//
//        @Override
//        public void run() {
//            System.out.println(this.getName() + " priority: " + this.getPriority());
//            int count = index.incrementAndGet();
//            switch (this.getPriority()) {
//                case Thread.MAX_PRIORITY :
//                    maxTimes.getAndAdd(count);
//                    break;
//                case Thread.NORM_PRIORITY :
//                    normTimes.getAndAdd(count);
//                    break;
//                case Thread.MIN_PRIORITY :
//                    minTimes.getAndAdd(count);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }


    static AtomicLong index = new AtomicLong(0);
    static AtomicLong minTimes = new AtomicLong(0);
    static AtomicLong normTimes = new AtomicLong(0);
    static AtomicLong maxTimes = new AtomicLong(0);

    public static void main(String[] args) {
        List<MyThread> minThreadList = new ArrayList<>();
        List<MyThread> normThreadList = new ArrayList<>();
        List<MyThread> maxThreadList = new ArrayList<>();

        int count = 1000;
        for (int i = 0; i < count; i++) {
            MyThread myThread = new MyThread("min----" + i);
            myThread.setPriority(Thread.MIN_PRIORITY);
            minThreadList.add(myThread);
        }
        for (int i = 0; i < count; i++) {
            MyThread myThread = new MyThread("norm---" + i);
            myThread.setPriority(Thread.NORM_PRIORITY);
            normThreadList.add(myThread);
        }
        for (int i = 0; i < count; i++) {
            MyThread myThread = new MyThread("max----" + i);
            myThread.setPriority(Thread.MAX_PRIORITY);
            maxThreadList.add(myThread);
        }

        for (int i = 0; i < count; i++) {
            maxThreadList.get(i).start();
            normThreadList.get(i).start();
            minThreadList.get(i).start();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("maxPriority 统计：" + maxTimes.get());
        System.out.println("normPriority 统计：" + normTimes.get());
        System.out.println("minPriority 统计：" + minTimes.get());
        System.out.println("普通优先级与最高优先级相差时间：" + (normTimes.get() - maxTimes.get()) + "ms");
        System.out.println("最低优先级与普通优先级相差时间：" + (minTimes.get() - normTimes.get()) + "ms");

    }

    static class MyThread extends Thread {

        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println(this.getName() + " priority: " + this.getPriority());
            switch (this.getPriority()) {
                case Thread.MAX_PRIORITY :
                    maxTimes.getAndAdd(System.currentTimeMillis());
                    break;
                case Thread.NORM_PRIORITY :
                    normTimes.getAndAdd(System.currentTimeMillis());
                    break;
                case Thread.MIN_PRIORITY :
                    minTimes.getAndAdd(System.currentTimeMillis());
                    break;
                default:
                    break;
            }
        }
    }



}



