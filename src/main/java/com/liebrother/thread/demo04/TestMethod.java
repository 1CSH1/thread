package com.liebrother.thread.demo04;

import org.junit.Test;

/**
 * @author James
 * @date 9/23/2019
 */
public class TestMethod {

    public static void main(String[] args) {
        Thread thread = new Thread();
        thread.start();
//        thread.join();
//        thread.join(1);
//        thread.join(1,1);
//        thread.stop();
//        thread.setDaemon();
//        thread.interrupt();
//        thread.isInterrupted();
//        thread.suspend();
//        thread.yield();
//        Thread.sleep();
//        Thread.sleep(1);
//        Thread.sleep(1,1);
//        thread.resume();
//
//        // Object
//        thread.notify();
//        thread.notifyAll();
//        thread.wait();
//        thread.wait(1);
//        thread.wait(1,1);

    }

    @Test
    public void testWaitNotify() {
        Refrigerator refrigerator = new Refrigerator();
        Producer producer = new Producer(refrigerator);
        Consumer consumer = new Consumer(refrigerator);
        producer.start();
        consumer.start();
//        try {
//            Thread.sleep(100000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    class Refrigerator {

        int index = 0;
        int count = 10;

        public synchronized void addBun() {
            while (index >= count) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            index ++;
            System.out.println("add index = " + index);
            this.notify();
        }

        public synchronized void removeBun() {
            while (index <= 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            index --;
            System.out.println("remove index = " + index);
            this.notify();
        }
    }

    class Producer extends Thread {

        private Refrigerator refrigerator;

        public Producer(Refrigerator refrigerator) {
            this.refrigerator = refrigerator;
        }

        @Override
        public void run() {
            while (true) {
                refrigerator.addBun();
            }
        }

    }

    class Consumer extends Thread {

        private Refrigerator refrigerator;

        public Consumer(Refrigerator refrigerator) {
            this.refrigerator = refrigerator;
        }

        @Override
        public void run() {
            while (true) {
                refrigerator.removeBun();
            }
        }

    }

}

