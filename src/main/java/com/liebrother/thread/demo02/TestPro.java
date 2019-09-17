package com.liebrother.thread.demo02;

public class TestPro {

    public static void main(String[] args) {
        MyThread myThread1 = new MyThread();
        myThread1.setName("aaa");
        myThread1.setPriority(Thread.MAX_PRIORITY);
        MyThread myThread2 = new MyThread();
        myThread2.setName("bbb");
        myThread2.setPriority(Thread.MAX_PRIORITY - 1);
        myThread1.start();
        myThread2.start();

    }



}

class MyThread extends Thread {

    @Override
    public void run() {
//        System.out.println(this.getName() + " begin ");
        System.out.println(this.getName() + " priority: " + this.getPriority());
//        System.out.println(this.getName() + " end ");
    }
}
