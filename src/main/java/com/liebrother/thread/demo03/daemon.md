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

上面第二点翻译过来是：**当所有用户线程都执行完，只存在守护线程在运行时，JVM 就退出**。看了网上资料以及一些书籍，全都有这句话，但是也都只是有这句话，没有讲明是为啥，好像这句话就成了定理，不需要证明的样子。既然咱最近搭建了 JVM Debug 环境，那就得来查个究竟。（查得好辛苦，花了很久的时间才查出来）

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

我在里面加了一些注释，可以发现，果然是我们想的那样，里面有记录着非守护线程的数量，而且当非守护线程为 1 时，就会唤醒在 `destory_vm()` 方法里面等待的线程，我们确认已经找到 JVM 在非守护线程数为 1 时会触发 JVM 退出的代码。紧接着我们看看 `destory_vm()` 代码，同样是在 `thread.cpp` 文件下。


```
bool Threads::destroy_vm() {
  JavaThread* thread = JavaThread::current();

#ifdef ASSERT
  _vm_complete = false;
#endif
  /**
   * 等待自己是最后一个非守护线程条件
   */
  // Wait until we are the last non-daemon thread to execute
  { MonitorLocker nu(Threads_lock);
    while (Threads::number_of_non_daemon_threads() > 1)
        /**
         * 非守护线程数大于 1，则一直等待
         */
      // This wait should make safepoint checks, wait without a timeout,
      // and wait as a suspend-equivalent condition.
      nu.wait(0, Mutex::_as_suspend_equivalent_flag);
  }

  /**
   * 下面代码是关闭 VM 的逻辑
   */
  EventShutdown e;
  if (e.should_commit()) {
    e.set_reason("No remaining non-daemon Java threads");
    e.commit();
  }
  ...... 省略余下代码
}
```

我们这里看到当非守护线程数量大于 1 时，就一直等待，直到剩下一个非守护线程时，就会在线程执行完后，退出 JVM。这时候又有一个点需要定位，什么时候调用 `destroy_vm()` 方法呢？还是通过查看代码以及注释，发现是在 `main()` 方法执行完成后触发的。

在 `java.c` 文件的 `JavaMain()` 方法里面，最后执行完调用了 `LEAVE()` 方法，该方法调用了 `(*vm)->DestroyJavaVM(vm); ` 来触发 JVM 退出，最终调用 `destroy_vm()` 方法。
```
#define LEAVE() \
    do { \
        if ((*vm)->DetachCurrentThread(vm) != JNI_OK) { \
            JLI_ReportErrorMessage(JVM_ERROR2); \
            ret = 1; \
        } \
        if (JNI_TRUE) { \
            (*vm)->DestroyJavaVM(vm); \
            return ret; \
        } \
    } while (JNI_FALSE)
```

所以我们也知道了，为啥 main 线程可以比子线程先退出？虽然 main 线程退出前调用了 `destroy_vm()` 方法，但是在 `destroy_vm()` 方法里面等待着非守护线程执行完，子线程如果是非守护线程，则 JVM 会一直等待，不会立即退出。

我们对这个点总结一下：**Java 程序在 main 线程执行退出时，会触发执行 JVM 退出操作，但是 JVM 退出方法 `destroy_vm()` 会等待所有非守护线程都执行完，里面是用变量 number_of_non_daemon_threads 统计非守护线程的数量，这个变量在新增线程和删除线程时会做增减操作**。

另外衍生一点就是：**当 JVM 退出时，所有还存在的守护线程会被抛弃，既不会执行 finally 部分代码，也不会执行 stack unwound 操作（也就是也不会 catch 异常）**。这个很明显，JVM 都退出了，守护线程自然退出了，当然这是守护线程的一个特性。

### 3. 是男是女？生下来就注定了

这个比较好理解，就是线程是用户线程还是守护线程，在线程一开始就得确定，在调用 `start()` 方法之前，还只是个对象，没有映射到 JVM 中的线程，这个时候可以修改 `daemon` 属性，调用 `start()` 方法之后，JVM 中就有一个线程映射这个线程对象，所以不能做修改了。

## 守护线程继承自父线程