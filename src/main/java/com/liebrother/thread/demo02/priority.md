![](http://www.liebrother.com/upload/99667eca2d1948979308f1eecb46dfdb_dxc_0006_01.jpg) 

> Java 多线程系列第 6 篇。

这篇我们来看看 Java 线程的优先级。

## Java 线程优先级

`Thread` 类中，使用如下属性来代表优先级。
  
```java
private int priority;
```

我们可以通过 `setPriority(int newPriority)` 来设置新的优先级，通过 `getPriority()` 来获取线程的优先级。 

有些资料通过下面的例子就得出了一个结论：~~**Java 线程默认优先级是 5**~~。     

```java
public static void main(String[] args) {
    Thread thread = new Thread();
    System.out.println(thread.getPriority());
}

// 打印结果：5
```

其实这是大错特错的，只是看到了表面，看看下面的例子，我们把当前线程的优先级改为 4，发现子线程 thread 的优先级也是 4。

```java
public static void main(String[] args) {
    Thread.currentThread().setPriority(4);
    Thread thread = new Thread();
    System.out.println(thread.getPriority());
}

// 打印结果：4
```

这啪啪啪打脸了，如果是线程默认优先级是 5，我们新创建的 thread 线程，没设置优先级，理应是 5，但实际是 4。我们看看 `Thread` 初始化 `priority` 的源代码。

```java
Thread parent = currentThread();
this.priority = parent.getPriority();
```

原来，线程默认的优先级是继承父线程的优先级，上面例子我们把父线程的优先级设置为 4，所以导致子线程的优先级也变成 4。

严谨一点说，**子线程默认优先级和父线程一样，Java 主线程默认的优先级是 5。**

Java 中定义了 3 种优先级，分别是`最低优先级（1）`、`正常优先级（5）`、`最高优先级（10）`，代码如下所示。Java 优先级范围是 **[1, 10]**，设置其他数字的优先级都会抛出 `IllegalArgumentException` 异常。

```java
/**
 * The minimum priority that a thread can have.
 */
public final static int MIN_PRIORITY = 1;

/**
 * The default priority that is assigned to a thread.
 */
public final static int NORM_PRIORITY = 5;

/**
 * The maximum priority that a thread can have.
 */
public final static int MAX_PRIORITY = 10;
```

接下来说说线程优先级的作用。先看下面代码，代码逻辑是创建了 3000 个线程，分别是： 1000 个优先级为 1 的线程， 1000 个优先级为 5 的线程，1000 个优先级为 10 的线程。用 `minTimes` 来记录 1000 个 `MIN_PRIORITY` 线程运行时时间戳之和，用 `normTimes` 来记录 1000 个 `NORM_PRIORITY` 线程运行时时间戳之和，用 `maxTimes` 来记录 1000 个 `MAX_PRIORITY` 线程运行时时间戳之和。通过统计每个优先级的运行的时间戳之和，值越小代表的就是越优先执行。我们运行看看。

```java
public class TestPriority {
    static AtomicLong minTimes = new AtomicLong(0);
    static AtomicLong normTimes = new AtomicLong(0);
    static AtomicLong maxTimes = new AtomicLong(0);

    public static void main(String[] args) {
        List<MyThread> minThreadList = new ArrayList<>();
        List<MyThread> normThreadList = new ArrayList<>();
        List<MyThread> maxThreadList = new ArrayList<>();

        int count = 1000;
        for (int i = 0; i < count; i++) {
            MyThread myThread = new MyThread("min----" + i);
            myThread.setPriority(Thread.MIN_PRIORITY);
            minThreadList.add(myThread);
        }
        for (int i = 0; i < count; i++) {
            MyThread myThread = new MyThread("norm---" + i);
            myThread.setPriority(Thread.NORM_PRIORITY);
            normThreadList.add(myThread);
        }
        for (int i = 0; i < count; i++) {
            MyThread myThread = new MyThread("max----" + i);
            myThread.setPriority(Thread.MAX_PRIORITY);
            maxThreadList.add(myThread);
        }

        for (int i = 0; i < count; i++) {
            maxThreadList.get(i).start();
            normThreadList.get(i).start();
            minThreadList.get(i).start();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("maxPriority 统计：" + maxTimes.get());
        System.out.println("normPriority 统计：" + normTimes.get());
        System.out.println("minPriority 统计：" + minTimes.get());
        System.out.println("普通优先级与最高优先级相差时间：" + (normTimes.get() - maxTimes.get()) + "ms");
        System.out.println("最低优先级与普通优先级相差时间：" + (minTimes.get() - normTimes.get()) + "ms");

    }

    static class MyThread extends Thread {

        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println(this.getName() + " priority: " + this.getPriority());
            switch (this.getPriority()) {
                case Thread.MAX_PRIORITY :
                    maxTimes.getAndAdd(System.currentTimeMillis());
                    break;
                case Thread.NORM_PRIORITY :
                    normTimes.getAndAdd(System.currentTimeMillis());
                    break;
                case Thread.MIN_PRIORITY :
                    minTimes.getAndAdd(System.currentTimeMillis());
                    break;
                default:
                    break;
            }
        }
    }
}
```

执行结果如下：
```
# 第一部分
max----0 priority: 10
norm---0 priority: 5
max----1 priority: 10
max----2 priority: 10
norm---2 priority: 5
min----4 priority: 1
.......
max----899 priority: 10
min----912 priority: 1
min----847 priority: 5
min----883 priority: 1

# 第二部分
maxPriority 统计：1568986695523243
normPriority 统计：1568986695526080
minPriority 统计：1568986695545414
普通优先级与最高优先级相差时间：2837ms
最低优先级与普通优先级相差时间：19334ms
```

我们一起来分析一下结果。先看看第一部分，最开始执行的线程高优先级、普通优先级、低优先级都有，最后执行的线程也都有各个优先级的，这说明了：**优先级高的线程不代表一定比优先级低的线程优先执行**。也可以换另一种说法：**代码执行顺序跟线程的优先级无关**。看看第二部分的结果，我们可以发现最高优先级的 1000 个线程执行时间戳之和最小，而最低优先级的 1000 个线程执行时间戳之和最大，因此可以得知：**一批高优先级的线程会比一批低优先级的线程优先执行**，即**高优先级的线程大概率比低优先的线程优先获得 CPU 资源**。

## 各操作系统中真有 10 个线程等级么？

Java 作为跨平台语言，线程有 10 个等级，但是映射到不同操作系统的线程优先级值不一样。接下来教大家怎么在 `OpenJDK` 源码中查各个操作系统中线程优先级映射的值。

* 看到 **Thread** 源代码，设置线程优先级最终调用了本地方法 `setPriority0()`；

```java
private native void setPriority0(int newPriority);
```

2. 接着我们在 **OpenJDK** 的 `Thread.c` 代码中找到 `setPriority0()` 对应的方法 `JVM_SetThreadPriority`；

```
static JNINativeMethod methods[] = {
    ...
    {"setPriority0",     "(I)V",       (void *)&JVM_SetThreadPriority},
    ...
};
```

3. 我们根据 `JVM_SetThreadPriority` 找到 **jvm.cpp** 中对应的代码段；

```
JVM_ENTRY(void, JVM_SetThreadPriority(JNIEnv* env, jobject jthread, jint prio))
  JVMWrapper("JVM_SetThreadPriority");
  // Ensure that the C++ Thread and OSThread structures aren't freed before we operate
  MutexLocker ml(Threads_lock);
  oop java_thread = JNIHandles::resolve_non_null(jthread);
  java_lang_Thread::set_priority(java_thread, (ThreadPriority)prio);
  JavaThread* thr = java_lang_Thread::thread(java_thread);
  if (thr != NULL) {                  // Thread not yet started; priority pushed down when it is
    Thread::set_priority(thr, (ThreadPriority)prio);
  }
JVM_END
```

4. 根据第 3 步中的代码，我们可以发现关键是 `java_lang_Thread::set_Priority()` 这段代码，继续找 **thread.cpp** 代码中的 `set_Priority()` 方法；

```
void Thread::set_priority(Thread* thread, ThreadPriority priority) {
  trace("set priority", thread);
  debug_only(check_for_dangling_thread_pointer(thread);)
  // Can return an error!
  (void)os::set_priority(thread, priority);
}
```

5. 发现上面代码最终调用的是 `os::set_priority()`，接着继续找出 **os.cpp** 的 `set_priority()` 方法；

```
OSReturn os::set_priority(Thread* thread, ThreadPriority p) {
#ifdef ASSERT
  if (!(!thread->is_Java_thread() ||
         Thread::current() == thread  ||
         Threads_lock->owned_by_self()
         || thread->is_Compiler_thread()
        )) {
    assert(false, "possibility of dangling Thread pointer");
  }
#endif

  if (p >= MinPriority && p <= MaxPriority) {
    int priority = java_to_os_priority[p];
    return set_native_priority(thread, priority);
  } else {
    assert(false, "Should not happen");
    return OS_ERR;
  }
}
```

6. 终于发现了最终转换为各操作系统的优先级代码 `java_to_os_priority[p]`，接下来就是找各个操作系统下的该数组的值。比如下面是 **Linux** 系统的优先级值。

```
int os::java_to_os_priority[CriticalPriority + 1] = {
  19,              // 0 Entry should never be used

   4,              // 1 MinPriority
   3,              // 2
   2,              // 3

   1,              // 4
   0,              // 5 NormPriority
  -1,              // 6

  -2,              // 7
  -3,              // 8
  -4,              // 9 NearMaxPriority

  -5,              // 10 MaxPriority

  -5               // 11 CriticalPriority
};
``` 

好了，大家应该知道怎么找出 Java 线程优先级 [1,10] 一一对应各个操作系统中的优先级值。下面给大家统计一下。

|Java 线程优先级|Linux|Windows|Apple|Bsd|Solaris|
|:---|:---|:---|:---|:---|:---|
|1|4|-2|27|0|0|
|2|3|-2|28|3|32|
|3|2|-1|29|6|64|
|4|1|-1|30|10|96|
|5|0|0|31|15|127|
|6|-1|0|32|18|127|
|7|-2|1|33|21|127|
|8|-3|1|34|25|127|
|9|-4|2|35|28|127|
|10|-5|2|36|31|127|

Windows 系统的在 **OpenJDK** 源码中只找到下面的常量。

```
THREAD_PRIORITY_LOWEST
THREAD_PRIORITY_BELOW_NORMAL
THREAD_PRIORITY_NORMAL
THREAD_PRIORITY_ABOVE_NORMAL
THREAD_PRIORITY_HIGHEST
```

对应的值是通过微软提供的函数接口文档查到的，链接在这：https://docs.microsoft.com/en-us/windows/win32/api/processthreadsapi/nf-processthreadsapi-setthreadpriority?redirectedfrom=MSDN

我们从这个表格中也可以发现一些问题，即使我们在 Java 代码中设置了比较高的优先级，其实映射到操作系统的线程里面，并不一定比设置了低优先级的线程高，很有可能是相同的优先级。看看 **Solaris 操作系统** 这个极端的例子，优先级 5 到 10 映射的是相同的线程等级。

回头想想上面的例子为什么 3000 个线程，`MAX_PRIORITY` 优先级的 1000 个线程会优先执行呢？因为我们的 3 个优先级分别映射到 **Windows** 操作系统线程的 3 个不同的等级，所以才会生效。假设将 1、5、10 改成 5、6、7，运行结果那就不大一样了。

最后记住：**切莫把线程优先级当做银弹，优先级高的线程不一定比优先级低的线程优先执行**。

这篇**线程优先级**文章也告段落了，朋友们看完觉得有用麻烦帮点个`在看`，推荐给身边朋友看看，原创不易。

`推荐阅读`：

[线程最最基础的知识](https://mp.weixin.qq.com/s/NSlEeXMK22-clfDv44h60w)

[老板叫你别阻塞了](https://mp.weixin.qq.com/s/cIj_uzT6gZjROO44rNFHFQ)

[吃个快餐都能学到串行、并行、并发](https://mp.weixin.qq.com/s/Euc2NKvK_TsqvcT-DWpD5A)

[泡一杯茶，学一学同异步](https://mp.weixin.qq.com/s/yWqFw_S7suYpqszuJFDsGg)

[进程知多少？](https://mp.weixin.qq.com/s/HJIVxnzyDesYPGGyJsaFyQ)

[设计模式看了又忘，忘了又看？](https://mp.weixin.qq.com/s/WiPwb7AyVlxyr1_kYXt96w)

**后台回复『设计模式』可以获取《一故事一设计模式》电子书**

`觉得文章有用帮忙转发&点赞，多谢朋友们！`

![LieBrother](http://www.liebrother.com/upload/c50a23a8826d45a7b66b3be24c89205e_.jpg)