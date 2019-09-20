
> Java 多线程系列第 6 篇。

这篇我们来看看 Java 线程的优先级。

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

这啪啪啪打脸了，如果是线程默认优先级是 5，我们新创建的 thread 线程，没设置优先级，理应是 5，但实际是 4。我们看看 `Thread` 源代码。

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

我们一起来分析一下结果。先看看第一部分，最开始执行的线程高优先级、普通优先级、低优先级都有，最后执行的线程也都有各个优先级的，这说明了：**优先级高的线程不代表一定比优先级低的线程优先执行**。看看第二部分的结果，我们可以发现最高优先级的 1000 个线程执行时间戳之和最小，而最低优先级的 1000 个线程执行时间戳之和最大，因此可以得知：**一批高优先级的线程会比一批低优先级的线程优先执行完成**


### 默认优先级

### 优先级 3 种级别

### 父子线程的优先级

来表示线程优先级的大小，平时我们创建一个线程，经常忽略掉设置优先级，可知，优先级不一定得设置，看看 `Thread` 源码，可以发现默认情况下，优先级是继承父线程的。




