package com.liebrother.thread.demo02;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    static AtomicInteger index = new AtomicInteger(0);
    static AtomicInteger minTimes = new AtomicInteger(0);
    static AtomicInteger normTimes = new AtomicInteger(0);
    static AtomicInteger maxTimes = new AtomicInteger(0);

    public static void main(String[] args) {
        List<MyThread> minThreadList = new ArrayList<>();
        List<MyThread> normThreadList = new ArrayList<>();
        List<MyThread> maxThreadList = new ArrayList<>();



        int count = 100;
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
            minThreadList.get(i).start();
            normThreadList.get(i).start();
            maxThreadList.get(i).start();
        }
    }

    static class MyThread extends Thread {

        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
//        System.out.println(this.getName() + " begin ");
            int count = index.incrementAndGet();
            switch ()
            if (Thread.MIN_PRIORITY == this.getPriority()) {
                minTimes.getAndAdd(count);
            }

            System.out.println(this.getName() + " priority: " + this.getPriority());
//        System.out.println(this.getName() + " end ");
        }
    }

}



