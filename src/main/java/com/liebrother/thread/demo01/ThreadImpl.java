package com.liebrother.thread.demo01;

public class ThreadImpl {

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        Thread myRunnable = new Thread(new MyRunnable());
        System.out.println("main Thraed begin");
        myThread.start();
        myRunnable.start();
        System.out.println("main Thraed end");
    }

}

class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("MyThread");
    }

}

class MyRunnable implements Runnable {

    public void run() {
        System.out.println("MyRunnable");
    }
}