package com.liebrother.thread.demo03;

/**
 * @author James
 * @date 9/24/2019
 */
public class TestDaemon {

    public static void main(String[] args) {
        MyThread myThread1 = new MyThread("daemonThread", 1000);
        myThread1.setDaemon(true);
        myThread1.start();
        MyThread myThread2 = new MyThread("nodaemonThread", 100);
        myThread2.start();
        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("JVM Exit!");
            }
        });
    }

}

class MyThread extends Thread {

    private int count;

    public MyThread(String name, int count) {
        super(name);
        this.count = count;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < count; i++) {
                System.out.println(this.getName() + i);
//                Thread.sleep(1000000000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(this.getName() + "-" + this.isDaemon() + "-" + "finally");
        }
    }
}