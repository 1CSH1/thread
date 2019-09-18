package com.liebrother.thread.demo02;

public class TestPro {

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

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getThreadGroup().getMaxPriority());
        ThreadGroup threadGroup = new ThreadGroup("myThreadGroup");
        threadGroup.setMaxPriority(5);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i ++) {
            threads[i] = new Thread(threadGroup, "myThread-" + i);
            threads[i].setPriority(7);
        }
        for (int i = 0; i < 10; i ++) {
            System.out.println(threads[i].getPriority());
        }
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

