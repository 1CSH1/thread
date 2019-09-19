
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

接下来说说线程优先级的作用。


### 默认优先级

### 优先级 3 种级别

### 父子线程的优先级

来表示线程优先级的大小，平时我们创建一个线程，经常忽略掉设置优先级，可知，优先级不一定得设置，看看 `Thread` 源码，可以发现默认情况下，优先级是继承父线程的。




