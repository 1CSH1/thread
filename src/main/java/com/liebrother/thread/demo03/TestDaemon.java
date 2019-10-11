package com.liebrother.thread.demo03;

import com.liebrother.thread.demo02.TestPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author James
 * @date 9/24/2019
 */

public class TestDaemon {
    static AtomicLong daemonTimes = new AtomicLong(0);
    static AtomicLong userTimes = new AtomicLong(0);

    public static void main(String[] args) {
        int count = 2000;
        List<MyThread> threads = new ArrayList<>(count);
        for (int i = 0; i < count; i ++) {
            MyThread daemonThread = new MyThread();
            daemonThread.setDaemon(true);
            daemonThread.setPriority(Thread.NORM_PRIORITY);
            threads.add(daemonThread);

            MyThread userThread = new MyThread();
            userThread.setDaemon(false);
            userThread.setPriority(Thread.NORM_PRIORITY);
            threads.add(userThread);
        }

        for (int i = 0, j = count -1; i < 1000 && j >= 1000; i++, j--) {
            threads.get(i).start();
            threads.get(j).start();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("daemon 统计：" + daemonTimes.get());
        System.out.println("user 统计：" + userTimes.get());
        System.out.println("daemon 和 user 相差时间：" + (daemonTimes.get() - userTimes.get()) + "ms");

    }

    static class MyThread extends Thread {
        @Override
        public void run() {
            if (this.isDaemon()) {
                daemonTimes.getAndAdd(System.currentTimeMillis());
            } else {
                userTimes.getAndAdd(System.currentTimeMillis());
            }
        }
    }
}

/**
public class TestDaemon {

    static AtomicLong daemonTimes = new AtomicLong(0);
    static AtomicLong userTimes = new AtomicLong(0);


    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        CyclicBarrier cyclicBarrier = new CyclicBarrier(2000, new MyRunnable());

        int count = 1000;
        for (int i = 0; i < count; i++) {
            Thread userThread = new Thread(new MyUserRunnable(cyclicBarrier));
            userThread.setPriority(Thread.MAX_PRIORITY);
            userThread.setDaemon(false);
            threads.add(userThread);

            Thread daemonThread = new Thread(new MyDaemonRunnable(cyclicBarrier));
            daemonThread.setPriority(Thread.MIN_PRIORITY);
            daemonThread.setDaemon(true);
            threads.add(daemonThread);
        }

        for (int i = 0; i < count * 2; i ++) {
            threads.get(i).start();
        }

        Thread.sleep(15000);

        System.out.println("daemon 统计：" + daemonTimes.get());
        System.out.println("user 统计：" + userTimes.get());
        System.out.println("daemon 和 user 相差时间：" + (daemonTimes.get() - userTimes.get()) + "ms");
    }


    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("等待开始");
        }
    }

    static class MyUserRunnable implements Runnable {

        private CyclicBarrier cyclicBarrier;

        public MyUserRunnable(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                this.cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            userTimes.getAndAdd(System.currentTimeMillis());
        }
    }

    static class MyDaemonRunnable implements Runnable {

        private CyclicBarrier cyclicBarrier;

        public MyDaemonRunnable(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                this.cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            daemonTimes.getAndAdd(System.currentTimeMillis());
        }
    }
}
**/


//public class TestDaemon {
//
//    public static void main(String[] args) {
//        MyThread myThread1 = new MyThread("daemonThread", 1000);
//        myThread1.setDaemon(true);
//        myThread1.start();
//        MyThread myThread2 = new MyThread("nodaemonThread", 100);
//        myThread2.start();
//        try {
//            Thread.sleep(100000000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                System.out.println("JVM Exit!");
//            }
//        });
//    }
//
//}
//
//class MyThread extends Thread {
//
//    private int count;
//
//    public MyThread(String name, int count) {
//        super(name);
//        this.count = count;
//    }
//
//    @Override
//    public void run() {
//        try {
//            for (int i = 0; i < count; i++) {
//                System.out.println(this.getName() + i);
////                Thread.sleep(1000000000);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            System.out.println(this.getName() + "-" + this.isDaemon() + "-" + "finally");
//        }
//    }
//}