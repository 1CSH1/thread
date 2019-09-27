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

上面第二点翻译过来是：当所有用户线程都执行完，只存在守护线程在运行时，JVM 就退出。

守护线程与普通线程唯一的区别是：当线程退出时，JVM 会检查其他正在运行的线程，如果这些线程都是守护线程，那么 JVM 会正常退出操作，但是如果有普通线程还在运行，JVM 是不会执行退出操作的。当 JVM 退出时，所有仍然存在的守护线程都将被抛弃，既不会执行 finally 部分的代码，也不会执行 stack unwound 操作，JVM 会直接退出。

### 3. 是男是女？生下来就注定了


## 守护线程继承自父线程