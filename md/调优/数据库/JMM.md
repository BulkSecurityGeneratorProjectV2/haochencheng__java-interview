### JMM
    Java内存模型即Java Memory Model，简称JMM    
Java内存模型定义了多线程之间共享变量的可见性以及如何在需要的时候对共享变量进行同步

### java运行机制
    Java源码通过javac翻译成字节码，由即时编译器(JIT)编译执行。因为字节码是静态代码，需要加载到内存才能成为可以动态运行的对象。

###    运行时数据模型
![image](https://upload-images.jianshu.io/upload_images/2654250-152293461531f520.png?imageMogr2/auto-orient/)
两种类型:

所有线程共享的数据区：
    
    1.方法区：储存已被虚拟机加载的类信息、常量、静态变量
    2.堆区：  我们常说用于存放对象的区域

线程私有(隔离)数据区：

    1.虚拟机栈
        方法执行时创建一个栈帧，用于存储局部变量、操作数栈、动态链接、方法出口等信息。每个方法一个栈帧，互不干扰
    2.本地方法栈:           
        用于存放执行native方法的运行数据    
    3.程序计数器:       
        当前线程所执行的字节码的指示器，通过改变计数器来选取下一条需要执行的字节码指令    
堆区结构

![image](https://upload-images.jianshu.io/upload_images/2654250-59c54de8bc3465c8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)
### 线程之间的通信
1.共享内存 （java采用）     
2.消息传递。   

### Java内存模型
JMM决定一个线程对共享变量的写入何时对另一个线程可见            

线程之间的共享变量存储在主内存（main memory）中，每个线程都有一个私有的本地内存（local memory），本地内存中存储了该线程以读/写共享变量的副本。

![image](http://ifeve.com/wp-content/uploads/2013/01/221.png)

1. 首先，线程A把本地内存A中更新过的共享变量刷新到主内存中去。
2. 然后，线程B到主内存中去读取线程A之前已更新过的共享变量。 

#### 共享内存存在的问题
#####    1.共享变量的可见性
A、B同时含有x的变量副本，A对x+1，B对x+1，A修改后没有刷新到主存.导致B对A的修改不可见。   
#####   2.竞争现象
如果多个线程共享一个对象，如果它们同时修改这个共享对象，这就产生了竞争现象。

###    支撑Java内存模型的基础原理
####    指令重排序
在执行程序时，为了提高性能，编译器和处理器会对指令做重排序。但是，JMM确保在不同的编译器和不同的处理器平台之上，通过插入特定类型的Memory Barrier来禁止特定类型的编译器重排序和处理器重排序，为上层提供一致的内存可见性保证。

编译器优化重排序：编译器在不改变单线程程序语义的前提下，可以重新安排语句的执行顺序。
指令级并行的重排序：如果不存l在数据依赖性，处理器可以改变语句对应机器指令的执行顺序。
内存系统的重排序：处理器使用缓存和读写缓冲区，这使得加载和存储操作看上去可能是在乱序执行。

####    数据依赖性
如果两个操作访问同一个变量，其中一个为写操作，此时这两个操作之间存在数据依赖性。 
编译器和处理器不会改变存在数据依赖性关系的两个操作的执行顺序，即不会重排序。

####    as-if-serial
不管怎么重排序，单线程下的执行结果不能被改变，编译器、runtime和处理器都必须遵守as-if-serial语义。

####    内存屏障（Memory Barrier ）
上面讲到了，通过内存屏障可以禁止特定类型处理器的重排序，从而让程序按我们预想的流程去执行。内存屏障，又称内存栅栏，是一个CPU指令，基本上它是一条这样的指令：

保证特定操作的执行顺序。
影响某些数据（或则是某条指令的执行结果）的内存可见性。
编译器和CPU能够重排序指令，保证最终相同的结果，尝试优化性能。插入一条Memory Barrier会告诉编译器和CPU：不管什么指令都不能和这条Memory Barrier指令重排序。


####    happens-before
从jdk5开始，java使用新的JSR-133内存模型，基于happens-before的概念来阐述操作之间的内存可见性。

    与程序员密切相关的happens-before规则如下：
    程序顺序规则：一个线程中的每个操作，happens-before于该线程中任意的后续操作。
    监视器锁规则：对一个锁的解锁操作，happens-before于随后对这个锁的加锁操作。
    volatile域规则：对一个volatile域的写操作，happens-before于任意线程后续对这个volatile域的读。
    传递性规则：如果 A happens-before B，且 B happens-before C，那么A happens-before C。

####    内存可见性
#####   volatile
Java语言规范对volatile的定义如下：

    Java编程语言允许线程访问共享变量，为了确保共享变量能被准确和一致地更新，线程应该确保通过排他锁单独获得这个变量。   
    
    1.volatile修饰的变量每次修改会使工作内存刷新到主内存
    2.清空其他工做内存中该变量的值

java虚拟机规范（jvm spec）中，规定了声明为volatile的long和double变量的get和set操作是原子的。这也说明了为什么将long和double类型的变量用volatile修饰，就可以保证对他们的赋值操作的原子性了

关于volatile变量的使用建议：多线程环境下需要共享的变量采用volatile声明；如果使用了同步块或者是常量，则没有必要使用volatile。

#####   synchronized

    synchronized可以保证方法或者代码在运行时，同一时刻只有一个方法可以进入临界区，同时还可以保存共享变量内存的可见性
    
    Java中每一个对象都可以作为锁，这是synchronized实现同步的基础：

普通同步方法，锁是当前实例对象

静态同步方法，锁是当前类的class对象

同步方法块，锁是括号里面的对象

通过 monitorenter 和 monitorexit 指令,即同步代码块

    在JVM中，对象在内存中的布局分为三块区域：对象头、实例数据和对齐填充


####    锁优化
jdk1.6对锁的实现引入了大量的优化，如自旋锁、适应性自旋锁、锁消除、锁粗化、偏向锁、轻量级锁等技术来减少锁操作的开销。    
锁主要存在四中状态，依次是：
无锁状态、  
偏向锁状态、    
轻量级锁状态、  
重量级锁状态，  
他们会随着竞争的激烈而逐渐升级。注意锁可以升级不可降级，这种策略是为了提高获得锁和释放锁的效率。

#### 自旋锁
线程的阻塞和唤醒需要CPU从用户态转为核心态，频繁的阻塞和唤醒对CPU来说是一件负担很重的工作       
所谓自旋锁，就是让该线程等待一段时间，不会被立即挂起，看持有锁的线程是否会很快释放锁

####    适应自旋锁
JDK 1.6引入了更加聪明的自旋锁，即自适应自旋锁。所谓自适应就意味着自旋的次数不再是固定的，它是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定。          
线程如果自旋成功了，那么下次自旋的次数会更加多

####    锁消除
为了保证数据的完整性，我们在进行操作时需要对这部分操作进行同步控制，但是在有些情况下，JVM检测到不可能存在共享数据竞争，这是JVM会对这些同步锁进行锁消除。锁消除的依据是逃逸分析的数据支持。

#### 锁粗化
锁粗话概念比较好理解，就是将多个连续的加锁、解锁操作连接在一起，扩展成一个范围更大的锁

####    轻量级锁
引入轻量级锁的主要目的是在多没有多线程竞争的前提下，减少传统的重量级锁使用操作系统互斥量产生的性能消耗

####    重量级锁
重量级锁通过对象内部的监视器（monitor）实现，其中monitor的本质是依赖于底层操作系统的Mutex Lock实现，操作系统实现线程之间的切换需要从用户态到内核态的切换，切换成本非常高。

####    CAS
CAS 操作包含三个操作数 —— 内存位置（V）、预期原值（A）和新值(B)。 如果内存位置的值与预期原值相匹配，那么处理器会自动将该位置值更新为新值 。否则，处理器不做任何操作


### jva类加载机制
- 启动类加载器 Bootstrap ClassLoader
  
    它负责加载存放在JDK\jre\lib(JDK代表JDK的安装目录，下同)下，或被-Xbootclasspath参数指定的路径中的，并且能被虚拟机识别的类库（如rt.jar，所有的java.*开头的类均被Bootstrap ClassLoader加载）。启动类加载器是无法被Java程序直接引用的。
- 扩展类加载器
  
    该加载器由sun.misc.Launcher$ExtClassLoader实现，它负责加载JDK\jre\lib\ext目录中，或者由java.ext.dirs系统变量指定的路径中的所有类库（如javax.*开头的类），开发者可以直接使用扩展类加载器。
    
- 应用程序类加载器

    该类加载器由sun.misc.Launcher$AppClassLoader来实现，它负责加载用户类路径（ClassPath）所指定的类，开发者可以直接使用该类加载器，如果应用程序中没有自定义过自己的类加载器，一般情况下这个就是程序中默认的类加载器。


- 自定义类加载器