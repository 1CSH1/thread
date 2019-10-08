> Java 多线程系列第 7 篇。

这篇我们来讲讲线程的另一个特性：守护线程 or 用户线程？

我们先来看看 `Thread.setDaemon()` 方法的注释，如下所示。

```
1. Marks this thread as either a daemon thread or a user thread. 
2. The Java Virtual Machine exits when the only threads running are all daemon threads.
3. This method must be invoked before the thread is started.
```

里面提到了 3 点信息，一一来做下解释：

### 1. 用户线程 or 守护线程？

把 Java 线程分成 2 类，一类是**用户线程**，也就是我们创建线程时，默认的一类线程，属性 `daemon = false`；另一类是**守护线程**，当我们设置 `daemon = true` 时，就是这类线程。

两者的一般关系是：用户线程就是运行在前台的线程，守护线程就是运行在后台的线程，一般情况下，守护线程是为用户线程提供一些服务。比如在 Java 中，守护线程

### 2. JVM 与用户线程共存亡

上面第二点翻译过来是：**当所有用户线程都执行完，只存在守护线程在运行时，JVM 就退出**。看了网上资料以及一些书籍，全都有这句话，但是都也只是有这句话，没有讲明是为啥，好像这句话就成了定理，不需要证明的样子。既然咱最近搭建了 JVM Debug 环境，那就得来查个究竟。（查得好辛苦，花了很久的时间才查出来）

我们看到 JVM 源码 `thread.cpp` 文件，这里是实现线程的代码。我们通过上面那句话，说明是有一个地方监测着当前非守护线程的数量，不然怎么知道现在只剩下守护线程呢？很有可能是在移除线程的方法里面，跟着这个思路，我们看看该文件的 `remove()` 方法。代码如下。
```
/**
 * 移除线程 p
 */
void Threads::remove(JavaThread* p, bool is_daemon) {

  // Reclaim the ObjectMonitors from the omInUseList and omFreeList of the moribund thread.
  ObjectSynchronizer::omFlush(p);

  /**
   * 创建一个监控锁对象 ml
   */
  // Extra scope needed for Thread_lock, so we can check
  // that we do not remove thread without safepoint code notice
  { MonitorLocker ml(Threads_lock);

    assert(ThreadsSMRSupport::get_java_thread_list()->includes(p), "p must be present");

    // Maintain fast thread list
    ThreadsSMRSupport::remove_thread(p);

    // 当前线程数减 1
    _number_of_threads--;
    if (!is_daemon) {
        /**
         * 非守护线程数量减 1
         */
      _number_of_non_daemon_threads--;

      /**
       * 当非守护线程数量为 1 时，唤醒在 destroy_vm() 方法等待的线程
       */
      // Only one thread left, do a notify on the Threads_lock so a thread waiting
      // on destroy_vm will wake up.
      if (number_of_non_daemon_threads() == 1) {
        ml.notify_all();
      }
    }
    /**
     * 移除掉线程
     */
    ThreadService::remove_thread(p, is_daemon);

    // Make sure that safepoint code disregard this thread. This is needed since
    // the thread might mess around with locks after this point. This can cause it
    // to do callbacks into the safepoint code. However, the safepoint code is not aware
    // of this thread since it is removed from the queue.
    p->set_terminated_value();
  } // unlock Threads_lock

  // Since Events::log uses a lock, we grab it outside the Threads_lock
  Events::log(p, "Thread exited: " INTPTR_FORMAT, p2i(p));
}
```

我在里面加了一些注释，可以发现，果然是我们想的那样，里面有记录着非守护线程的数量，而且当非守护线程为 1 时，就会唤醒在 `destory_vm()` 方法里面等待的线程，我们确认已经找到 JVM 在非守护线程数为 1 时会触发 JVM 退出的代码。紧接着我们看看 `destory_vm()` 代码。



守护线程与普通线程唯一的区别是：当线程退出时，JVM 会检查其他正在运行的线程，如果这些线程都是守护线程，那么 JVM 会正常退出操作，但是如果有普通线程还在运行，JVM 是不会执行退出操作的。当 JVM 退出时，所有仍然存在的守护线程都将被抛弃，既不会执行 finally 部分的代码，也不会执行 stack unwound 操作，JVM 会直接退出。

### 3. 是男是女？生下来就注定了


## 守护线程继承自父线程