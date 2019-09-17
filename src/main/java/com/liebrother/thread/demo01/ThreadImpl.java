package com.liebrother.thread.demo01;

public class ThreadImpl {


    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        myThread.setName("MyThread");
        Thread myRunnable = new Thread(new MyRunnable());
        myRunnable.setName("MyRunnable");
        System.out.println("main Thraed begin");
        myThread.start();
        myRunnable.start();
        System.out.println("main Thraed end");
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
//
//    public static void main(String[] args) {
//        MyThread myThread = new MyThread();
//        myThread.setName("MyThread");
//        Thread myRunnable = new Thread(new MyRunnable());
//        myRunnable.setName("MyRunnable");
//        System.out.println("main Thraed begin");
//        myThread.run();
//        myRunnable.run();
//        System.out.println("main Thraed end");
//        try {
//            Thread.sleep(1000000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

}

class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("MyThread");
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class MyRunnable implements Runnable {

    public void run() {
        System.out.println("MyRunnable");
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}