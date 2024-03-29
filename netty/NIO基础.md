# NIO基础

> 全称 ： non-blocking IO 非阻塞IO

**阻塞和非阻塞、同步和异步的概念**

阻塞和非阻塞：是针对于线程来说的

- 阻塞：当前线程挂起
- 非阻塞：当前线程不会挂起，可以处理其他事情
- 区别：线程是否被挂起

同步和异步：是针对于代码来说的

- 同步：等待当前代码执行完成，并获取到结果，再继续支持下一行代码
- 异步：不等待代码执行完成，立即执行下一行代码，通过回调或其他方式拿到运行结果
- 区别：获取结果的方式

**小明去吃饭**

小明到了饭店

1. 情况1：
   1. 点了饭，等待上饭，期间玩手机（非阻塞），每过一段时间问下服务员饭好了没有（同步）。（小明此时是**同步非阻塞**的）
   2. 玩了会手机没啥玩的了，放下手机，也没有其他事情做（阻塞），每过一段时间问下服务员饭好了没有（同步）。（小明此时是**同步阻塞**的）
2. 情况2
   1. 点了饭，等待上饭，期间玩手机（非阻塞），等服务员端来饭（异步）。（小明此时是**异步非阻塞**的）
   2. 玩了会手机没啥玩的了，放下手机，也没有其他事情做（阻塞），等服务员端来饭（异步）。（小明此时是**异步阻塞**的）





## 1. 三大组件：Buffer、Channel 、Selector

### 1.1 Buffer 

Buffer用来**缓冲数据**，常见的buffer：

- **ByteBuffer**  常用
  - MappedByteBuffer
  - DirectByteBuffer
  - HeapByteBuffer
- ShortBuffer
- IntBuffer
- LongBuffer
- FloatBuffer
- DoubleBuffer
- CharBuffer

### 1.2 Channel

Channel 是读取数据的**双向通道**，可以从channel读取数据后写入buffer，也可以将buffer的数据写入到channel。

```mermaid
graph LR
c(channel) --> b(buffer)
b-->c
```

常用的Channel：

- FileChannel：文件的数据通道
- DatagramChannel：UDP协议的数据通道
- SocketChannel：TCP的数据通道，客户端或服务端都可用
- ServerSocketChannel：TCP的数据通道，服务端专用

### 1.3 Selector

用于在一个线程中管理多个channel，节省CPU和内存的消耗，在同服务器配置下提高程序的性能

#### 多线程版本

```mermaid
graph TD
subgraph 多线程 
    thread1-->socket1
    thread2-->socket2
    thread3-->socket3
end

```

##### ⚠️多线程缺点

- 内存占用高（每个线程都会消耗内存）
- 线程上下切换成本高
- 仅适合连接数较少的场景

#### 线程池版本

```mermaid
graph TD
subgraph 线程池
t1(thread1) --> socket1
t1 -.-> socket3
t2(thread2) --> socket2
t2 -.-> socket4
end
```

线程池是阻塞模式的，只有线程处理完当前socket后才能处理下一个socket

##### ⚠️线程池缺点

- 阻塞模式，每个线程只能处理一个socket连接
- 仅适合短链接的场景

#### Selector版本

selector的作用是配合一个线程中管理多个channel，channel工作在非阻塞模式下，线程不会吊死在某个channel上。selector工作在阻塞模式下，等待channel的读写。

**适合连接数少，流量低的场景**

```mermaid
graph TD
subgraph selector
thread-->s1(selector)
s1-->channel1
s1-->channel2
s1-->channel3
end
```



调用 selector 的 select() 会阻塞直到 channel 发生了读写就绪事件，这些事件发生，select 方法就会返回这些事件交给 thread 来处理

## 2. ByteBuffer(字节缓冲)

### 2.1 基础使用

#### 2.1.1 读数据

```java
public void testRead(){
    try (RandomAccessFile file = new RandomAccessFile("abc.txt", "rw")) {
        FileChannel channel = file.getChannel();
        //创建一个 大小为11 的ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        while (true) {
            //从通道中读取数据到ByteBuffer，每次读取的数据是ByteBuffer的大小
            int len = channel.read(byteBuffer);
            //len表示读取了多少数据，0表示数据已全部读取
            if (len <= 0) {
                break;
            }
            //切换为读模式
            byteBuffer.flip();
            for (int i = 0; i < len; i++) {
                System.out.println((char) byteBuffer.get());
            }
            //切换为写模式
            byteBuffer.clear();
        }
    } catch (IOException ioException) {
    }
}
```

`abc.txt`文件内容

```tex
134567890abcdef
```

代码输出内容

```tex
1
3
4
5
6
7
8
9
0
a
b
c
d
e
f

```

[读数据示例](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

#### 2.1.2 写数据

```java
public void testWrite(){
    try (RandomAccessFile file = new RandomAccessFile("write.txt", "rw")) {
        FileChannel channel = file.getChannel();

        //创建ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        //往ByteBuffer中写入数据
        byteBuffer.put("test".getBytes());
        //切换为读模式
        byteBuffer.flip();

        //通过channel将ByteBuffer写入到文件中
        channel.write(byteBuffer);
    } catch (IOException ioException) {
    }
}
```

`write.txt`文件内没有内容，代码运行后内容如下：

```tex
test
```

[写数据示例](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

### 2.2 ByteBuffer的结构

**三个重要属性：**

* capacity：ByteBuffer的容量
* position：ByteBuffer的指针
* limit：ByteBuffer的读写限制

1、运行`ByteBuffer.allocate(10)`，创建了一个容量为10的ByteBuffer，三个重要属性如下：

![](./images/byteBuffer001.png)

2、写模式下（新建的`ByteBuffer`默认为写模式，当`clear()` 或者`compact()`动作发生时切换为写模式），`position`是待写入位置；`limit`和`capacity`相等，下图写入了5个字节后的状态：

![](./images/byteBuffer002.png)

3、读模式下，`position`是待读取位置，指向最前方的位置；`limit`是当前字节的个数（读取限制）；状态如下：

![](./images/byteBuffer003.png)

4、读取2个字节的状态：

![](./images/byteBuffer004.png)

5、切换为写模式，

​		5.1、情况1：执行`clear()`动作，状态如下：

![](./images/byteBuffer005.png)

​		[clear示例#testStructureClear](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

​		5.2、情况2：执行`compact()`动作，把未读取的数据向前压缩，状态如下：

![](./images/byteBuffer006.png)

​		[compact示例#testStructureCompact](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

### 2.3 ❤常用方法和示例

#### 2.3.1 写入数据：put 、clear 和 compact方法

##### put方法：写入数据使用

`public ByteBuffer put(byte value)`：写入一个字节，同时position向后移动一位

`public ByteBuffer put(byte[] values)`：写入多个字节，同时position向后移动多位（写入多少个字节就移动多少位）

`public ByteBuffer put(int index, byte value)`：根据位置写入一个字节，**position不会变动**

示例代码：

```java
public void testPut() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
    //写入单个字节，写入后 position 自动 +1
    byteBuffer.put((byte)'a');
    ByteBufferUtil.debugAll(byteBuffer);
    System.out.println();

    //写入多个字节，写入后 position 自动 + byte数组的长度
    byteBuffer.put(new byte[]{'b','c'});
    ByteBufferUtil.debugAll(byteBuffer);
    System.out.println();

    //指定位置写入字节，注意该方式 position 不会变动
    byteBuffer.put(2,(byte)'e');
    ByteBufferUtil.debugAll(byteBuffer);
}
```

<details>
    <summary>展开查看运行结果</summary>
<pre><code>
+--------+-------------------- all ------------------------+----------------+
position: [1], limit: [16]
        +-------------------------------------------------+
        |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 |a...............|
+--------+-------------------------------------------------+----------------+
<br>
+--------+-------------------- all ------------------------+----------------+
position: [3], limit: [16]
        +-------------------------------------------------+
        |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 00 00 00 00 00 00 00 00 00 00 00 00 00 |abc.............|
+--------+-------------------------------------------------+----------------+
<br>
+--------+-------------------- all ------------------------+----------------+
position: [3], limit: [16]
        +-------------------------------------------------+
        |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 65 00 00 00 00 00 00 00 00 00 00 00 00 00 |abe.............|
+--------+-------------------------------------------------+----------------+
</code></pre>
</details>
[put示例源码#testPut](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

##### clear方法：清除数据

clear方法实际上是移动了`postion`和`limit`，并未真正的清除数据，**将position移动到头的位置，limit移动到尾**

示例代码：

```java
public void testClear() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
    //写入多个字节
    byteBuffer.put(new byte[]{'a','b','c'});
    ByteBufferUtil.debugAll(byteBuffer);
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");

    //清空数据，实际上是移动了postion和limit，并未真正的清除数据
    byteBuffer.clear();
    ByteBufferUtil.debugAll(byteBuffer);
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");

    //覆盖之前的数据写入新数据
    byteBuffer.put((byte) 'd');
    ByteBufferUtil.debugAll(byteBuffer);
}
```

<details>
    <summary>展开查看运行结果</summary>
    <pre><code>
 +--------+-------------------- all ------------------------+----------------+
position: [3], limit: [16]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 00 00 00 00 00 00 00 00 00 00 00 00 00 |abc.............|
+--------+-------------------------------------------------+----------------+
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [0], limit: [16]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 00 00 00 00 00 00 00 00 00 00 00 00 00 |abc.............|
+--------+-------------------------------------------------+----------------+
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [1], limit: [16]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 64 62 63 00 00 00 00 00 00 00 00 00 00 00 00 00 |dbc.............|
+--------+-------------------------------------------------+----------------+
    </code></pre>
</details>

[clear示例源码#testClear](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

##### compact方法：压缩数据

compact方法把未读取的数据向前压缩，再将position移动至压缩后数据的尾端，limit移动到尾

示例代码：

```java
public void testCompact() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
    //写入多个字节
    byteBuffer.put(new byte[]{'a','b','c'});

    //读取两个字节
    byteBuffer.flip();
    byteBuffer.get();
    byteBuffer.get();
    ByteBufferUtil.debugAll(byteBuffer);

    //压缩数据
    byteBuffer.compact();
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓compact↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);

    //覆盖之前的数据写入新数据
    byteBuffer.put((byte) 'd');
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);
}
```

<details>
    <summary>展开查看运行结果</summary>
    <pre><code>
+--------+-------------------- all ------------------------+----------------+
position: [2], limit: [3]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 00 00 00 00 00 00 00 00 00 00 00 00 00 |abc.............|
+--------+-------------------------------------------------+----------------+
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓compact↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [1], limit: [16]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 62 63 00 00 00 00 00 00 00 00 00 00 00 00 00 |cbc.............|
+--------+-------------------------------------------------+----------------+
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [2], limit: [16]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 64 63 00 00 00 00 00 00 00 00 00 00 00 00 00 |cdc.............|
+--------+-------------------------------------------------+----------------+
    </code></pre>
</details>
[clear示例源码#testCompact](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

#### 2.3.2 读取数据：flip 和 get 方法

##### flip方法

**将limit移动到当前已写入数据的尾部（即position的所在的位置），position移动到头。**

源码：

```java
public final Buffer flip() {
    //limit移动到position的位置
    limit = position;
    //position移动到头
    position = 0;
    mark = -1;
    return this;
}
```

示例：

```java
public void testFlip(){
    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
    //写入多个字节
    byteBuffer.put("abcde".getBytes());
    ByteBufferUtil.debugAll(byteBuffer);
    //切换为读模式
    byteBuffer.flip();
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓flip↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);

    //读取全部数据
    int limit = byteBuffer.limit();
    for (int i = 0; i < limit; i++) {
        System.out.println(byteBuffer.get());
    }

    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);
}
```

<details>
	<summary>展开查看运行结果</summary>
    <pre><code>
+--------+-------------------- all ------------------------+----------------+
position: [5], limit: [16]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65 00 00 00 00 00 00 00 00 00 00 00 |abcde...........|
+--------+-------------------------------------------------+----------------+
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓flip↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [0], limit: [5]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65 00 00 00 00 00 00 00 00 00 00 00 |abcde...........|
+--------+-------------------------------------------------+----------------+
97
98
99
100
101
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [5], limit: [5]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65 00 00 00 00 00 00 00 00 00 00 00 |abcde...........|
+--------+-------------------------------------------------+----------------+
    </code></pre>
</details>

[flip示例源码#testFlip](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

##### get方法

读取值

`public byte get()`：以`position`为索引读取`ByteBuffer`对应的字节，**并将`position`后移一位（即`position++`）。**

`public byte get(int index)`：以参数为索引读取`ByteBuffer`对应的字节，**`position`不会变更。**

示例：

```java
public void testGet(){
    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
    //写入多个字节
    byteBuffer.put("abcde".getBytes());
    //切换为读模式
    byteBuffer.flip();
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓flip↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);

    //读取限制
    int limit = byteBuffer.limit();
    //通过索引读取，position值不变更
    for (int i = 0; i < limit; i=i+2) {
        System.out.println(byteBuffer.get(i));
    }
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get(index)↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);

    //使用position作为索引读取，position变更
    for (int i = 0; i < limit; i++) {
        System.out.println(byteBuffer.get());
    }
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get()↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);
}
```

<details>
	<summary>展开查看运行结果</summary>
    <pre><code>
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓flip↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [0], limit: [5]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65 00 00 00 00 00 00 00 00 00 00 00 |abcde...........|
+--------+-------------------------------------------------+----------------+
97
99
101
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get(index)↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [0], limit: [5]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65 00 00 00 00 00 00 00 00 00 00 00 |abcde...........|
+--------+-------------------------------------------------+----------------+
97
98
99
100
101
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get()↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [5], limit: [5]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65 00 00 00 00 00 00 00 00 00 00 00 |abcde...........|
+--------+-------------------------------------------------+----------------+
    </code></pre>
</details>

[get示例源码#testGet](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

#### 2.3.3 标记位置：make 和 reset方法

**不建议使用！！！！！**

make方法：标记当前`position`的值

reset方法：将`make()`记录的值恢复到`position`

[make、reset示例源码#testMakeAndReset](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

##### 不建议使用原因

reset方法在执行的时候，如果会判断是否标记，没有标记会抛出异常，而这个异常无法提前预知。

异常代码示例：

```java
public void testResetException(){
    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
    //写入多个字节
    byteBuffer.put("abcde".getBytes());
    //切换为读模式
    byteBuffer.flip();

    System.out.println(byteBuffer.get());
    System.out.println(byteBuffer.get());
    byteBuffer.mark();
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get()2↓↓↓make()↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);

    //position方法，当make值比 po
    byteBuffer.position(1);
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓position↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);

    System.out.println(byteBuffer.get());

    //发生异常，而且异常不可预知，只能try catch
    byteBuffer.reset();
}
```

最后一行`byteBuffer.reset();`会抛出异常

明明执行了`make()`为什么还抛出异常？

异常的原因：

先看`make() reset() position()`源码

```java
//标记变量
private int mark = -1;
public final Buffer mark() {
    //make变量记录下当前的position，即标记
    mark = position;
    return this;
}
public final Buffer position(int newPosition) {
    position = newPosition;
    //若make 大于 新指定的position 则make置为-1，也就是取消标记
    if (mark > position) mark = -1;
    return this;
}
public final Buffer reset() {
    int m = mark;
    //判断make是否在小于0，小于0则不存在抛出异常
    if (m < 0)
        throw new InvalidMarkException();
    //make存在 赋值给 position
    position = m;
    return this;
}
```

那么执行`make()`后再执行` position(int newPosition)`，如果`make`变量小于`newPosition`变量，`make`会被赋值为-1，接下来在执行`reset`会报错，结合上边的异常代码示例可以理解的更彻透。

[异常代码示例源码#testResetException](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

#### 2.3.4 字符串与 ByteBuffer 互转

```java
public void testString2ByteBuffer(){
    //字符串转ByteBuffer 方法1
    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
    byteBuffer.put("abcde".getBytes());
    //读模式
    byteBuffer.flip();

    //字符串转ByteBuffer  方法2：已经为读模式
    ByteBuffer byteBuffer1 = StandardCharsets.UTF_8.encode("12345");

    //字符串转ByteBuffer  方法3：已经为读模式
    ByteBuffer byteBuffer2 = ByteBuffer.wrap("abcde".getBytes());

    //ByteBuffer转字符串
    CharBuffer decode = StandardCharsets.UTF_8.decode(byteBuffer);
    System.out.println(decode);
    System.out.println(StandardCharsets.UTF_8.decode(byteBuffer1));
    System.out.println(StandardCharsets.UTF_8.decode(byteBuffer2));
}
```

**本质上是byte数组与char数组的互转**

[互转代码示例源码#testString2ByteBuffer](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

### 2.4 应用实例

#### 2.4.1 粘包、拆包

**模拟网络请求的粘包：每条数据以\n作为结尾**
数据1：say hi!\nhello
数据2： world~\nhi\n
将数据拆包并打印

```java
public void testStickingAndUnpacking() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
    //发送数据1
    //                     7     12
    byteBuffer.put("say hi!\nhello".getBytes());

    decodeData(byteBuffer);
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓compact↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);

    //发送数据2
    byteBuffer.put(" world~\nhi\n".getBytes());
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓data2↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);

    decodeData(byteBuffer);
    System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓compact↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    ByteBufferUtil.debugAll(byteBuffer);
}

/**
     * 解析数据
     * @param byteBuffer
     */
private void decodeData(ByteBuffer byteBuffer){
    //存储已读取的数据
    StringBuilder readBytes = new StringBuilder();
    //改为读模式，并获取limit
    int len = byteBuffer.flip().limit();
    for (int i = 0; i < len; i++) {
        byte currByte = byteBuffer.get(i);
        if(currByte == '\n'){
            //byte转string
            String data = new String(StringUtil.decodeHexDump(readBytes.toString()), StandardCharsets.UTF_8);
            System.out.println(data);
            //清空旧数据，准备接收新数据
            readBytes = new StringBuilder();
            //调整 position 保证 compact 运行后数据正确
            byteBuffer.position(i+1);
        } else {
            readBytes.append(StringUtil.byteToHexStringPadded(currByte));
        }
    }
    byteBuffer.compact();
}
```

<details>
	<summary>展开查看运行结果</summary>
    <pre><code>
say hi!
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓compact↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [5], limit: [16]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 68 65 6c 6c 6f 69 21 0a 68 65 6c 6c 6f 00 00 00 |helloi!.hello...|
+--------+-------------------------------------------------+----------------+
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓data2↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [16], limit: [16]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 68 65 6c 6c 6f 20 77 6f 72 6c 64 7e 0a 68 69 0a |hello world~.hi.|
+--------+-------------------------------------------------+----------------+
hello world~
hi
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓compact↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
+--------+-------------------- all ------------------------+----------------+
position: [0], limit: [16]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 68 65 6c 6c 6f 20 77 6f 72 6c 64 7e 0a 68 69 0a |hello world~.hi.|
+--------+-------------------------------------------------+----------------+
    </code></pre>
</details>
[粘包、拆包示例源码#testStickingAndUnpacking](./netty_demo/src/main/test/top/ersut/ByteBufferDemoTest.java)

## 3. 文件编程

### 3.1 FileChannel

#### ⚠️ FileChannel 工作模式

> FileChannel 只能工作在阻塞模式下，具体原因：操作系统的限制

#### 3.1.1 获取FileChannel

可以通过`FileOutputStream`、`FileInputStream`、`RandomAccessFile`的`getChannel`方法获取`FileChannel`。

- `FileOutputStream`：只可以写入文件（只写模式）
- `FileInputStream`：只可以读取文件（只读模式）
- `RandomAccessFile`：通过实例化时的`mode`参数决定文件的访问权限
  - mode为r时可以读取
  - mode为w时可以写入
  - mode为rw时可以写入和读取

```java
try (FileOutputStream fileOutputStream = new FileOutputStream("fileChannel.txt")){
    //获取通道
    FileChannel fileChannel = fileOutputStream.getChannel();
}

try (FileInputStream fileInputStream = new FileInputStream("fileChannel.txt")){
    //获取通道
    FileChannel fileChannel = fileInputStream.getChannel();
}
```

[示例代码#getFileChannelTest](./netty_demo/src/main/test/top/ersut/FileChannelTest.java)

```java
try (RandomAccessFile randomAccessFile = new RandomAccessFile("randomAccessFile.txt","rw")){
    FileChannel fileChannel = randomAccessFile.getChannel();
}
```

[示例代码#randomAccessFileTest](./netty_demo/src/main/test/top/ersut/FileChannelTest.java)

#### 3.1.2 写入文件

```java
//字符串转换ByteBuffer
ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(writeStr);

//FileChannel.write 无法保证一次性写入所有字节，所以要循环引用直到 ByteBuffer 中没有了字节
while (byteBuffer.hasRemaining()){
    fileChannel.write(byteBuffer);
}
```

**FileChannel.write 无法保证一次性写入所有字节，所以要循环引用直到 ByteBuffer 中没有了字节**

[示例代码#getFileChannelTest](./netty_demo/src/main/test/top/ersut/FileChannelTest.java)

#### 3.1.3 读取文件

```java
ByteBuffer byteBuffer = ByteBuffer.allocate(10);
//获取文件内容
fileChannel.read(byteBuffer);
```

[示例代码#getFileChannelTest](./netty_demo/src/main/test/top/ersut/FileChannelTest.java)

#### 3.1.4 通过FileChannel拷贝文件

```java
try(
        FileInputStream from = new FileInputStream("fileChannel.txt");
        FileOutputStream to = new FileOutputStream("fileChannel_copy.txt");
){
    FileChannel fromChannel = from.getChannel();
    FileChannel toChannel = to.getChannel();
    //单次拷贝只有2GB，超出2GB部分不会进行拷贝
    fromChannel.transferTo(0,fromChannel.size(),toChannel);
}
```

**注意：`FileChannel.transferTo`方法单次拷贝只有2GB，超出2GB部分不会进行拷贝**

[示例代码#fileChannelCopyTest](./netty_demo/src/main/test/top/ersut/FileChannelTest.java)

### 3.2 Files

#### 3.2.1 拷贝文件

```java

Path from = Paths.get("fileChannel.txt");
Path to = Paths.get("fileChannel_copy1.txt");
try {
    //该方式拷贝不限制文件的大小，目标文件存在会报错
    Files.copy(from,to);
}
```

该方式拷贝不限制文件的大小，**但是目标文件存在会报错**

可以通过`StandardCopyOption.REPLACE_EXISTING`，将目标文件覆盖

```java
try {
    //StandardCopyOption.REPLACE_EXISTING 代表如果目标文件存在进行覆盖操作
    Files.copy(from,to,StandardCopyOption.REPLACE_EXISTING);
}
```

[示例代码#filesCopyTest](./netty_demo/src/main/test/top/ersut/FileChannelTest.java)

#### 3.2.2 移动文件

```java
Path source = Paths.get("fileChannel_copy1.txt");
Path targer = Paths.get("fileChannel_copy2.txt");
try {
    //StandardCopyOption.ATOMIC_MOVE 代表移动文件，并保证文件的原子性,同时可进行重命名
    Files.move(source,targer,StandardCopyOption.ATOMIC_MOVE);
}
```

**`StandardCopyOption.ATOMIC_MOVE`代表移动文件，并保证文件的原子性,同时可进行重命名**

[示例代码#filesMoveTest](./netty_demo/src/main/test/top/ersut/FileChannelTest.java)

#### 3.2.3 删除文件/目录

删除文件：

```java
Path path = Paths.get("fileChannel_copy1.txt");
try {
    //删除文件
    Files.delete(path);
}
```

删除目录：

```java
try {
    String deleteDirName = "deleteDir";
    Path dir = Paths.get(deleteDirName);

    //删除目录，注意当目录下有文件，删除目录会报异常
    Files.delete(dir);
}
```

**注意当目录下有文件，删除目录会报异常**

[示例代码#filesDeleteTest](./netty_demo/src/main/test/top/ersut/FileChannelTest.java)

#### 3.2.4 遍历目录

```java
String deleteDirName = "deleteDir3";
Path dir = Paths.get(deleteDirName);

final StringBuilder tab = new StringBuilder();
Files.walkFileTree(dir,new SimpleFileVisitor<Path>(){
    //目录的前置回调
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        tab.append(" ");
        System.out.println(tab+dir.getFileName().toString());
        return super.preVisitDirectory(dir, attrs);
    }

    //文件的回调
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        System.out.println(tab+" |- "+file.getFileName());
        return super.visitFile(file,attrs);
    }
});
```

<details><summary>展开查看运行结果</summary><pre><code> deleteDir3
  |- 1.txt
  |- 2.txt
  |- 3.txt
  children
   |- c1.txt
</code></pre>
</details>

`Files.walkFileTree`方法是遍历文件树

`SimpleFileVisitor`类：文件树的实时回调

- `preVisitDirectory`方法：目录的前置回调
- `visitFile`方法：目录中文件的回调
- `visitFileFailed`方法： 文件访问失败的回调
- `postVisitDirectory`方法：目录的后置回调

#### 3.2.5 删除带文件目录

```java
String deleteDirName = "deleteDir2";
Path dir = Paths.get(deleteDirName);

//删除带文件的目录
try {
    //遍历文件树
    //SimpleFileVisitor 文件树的实时回调
    Files.walkFileTree(dir,new SimpleFileVisitor<Path>(){

        //文件的回调
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return super.visitFile(file,attrs);
        }

        //对目录的后置回调
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return super.postVisitDirectory(dir,exc);
        }
    });
}
```

##### ⚠️ 删除很危险

> 删除是危险操作，确保要递归删除的文件夹没有重要内容

## 4. 网络编程

### 4.1 阻塞模式 vs 非阻塞模式

#### 4.1.1 测试的客户端

1. 打开客户端

   ```java
   SocketChannel client = SocketChannel.open();
   ```

2. 连接服务端

   ```java
   client.connect(new InetSocketAddress("127.0.0.1",6666));
   ```

3. 发送消息

   ```java
   client.write(StandardCharsets.UTF_8.encode("hello!"))
   ```

[示例代码#socketChannelTest](./netty_demo/src/main/test/top/ersut/SocketChannelTest.java)：

```java
public void socketChannelTest() throws IOException, InterruptedException {
    //打开客户端
    SocketChannel client1 = SocketChannel.open();
    //连接服务端
    client1.connect(new InetSocketAddress("127.0.0.1",6666));
    //发送消息
    client1.write(StandardCharsets.UTF_8.encode("hello!"));
    Thread.sleep(3*1000);
    client1.write(StandardCharsets.UTF_8.encode("hello2!"));

    SocketChannel client2 = SocketChannel.open();
    client2.connect(new InetSocketAddress("127.0.0.1",6666));
    client2.write(StandardCharsets.UTF_8.encode("hi!"));
    Thread.sleep(3*1000);
    client2.write(StandardCharsets.UTF_8.encode("hi2!"));
    Thread.sleep(10*1000);
}
```

#### 4.1.2 阻塞的服务端

##### 4.1.2.1 创建阻塞的服务端

1. 打开服务端

   ```java
   ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
   ```

2. 绑定端口

   ```java
   serverSocketChannel.bind(new InetSocketAddress(6666));
   ```

3. 等待客户端

   ```java
   SocketChannel socketChannel = serverSocketChannel.accept();
   ```

   `accept()`方法会阻塞线程，等待新客户端的连接

4. 接收客户端的消息

   ```java
   ByteBuffer byteBuffer = ByteBuffer.allocate(16);
   socketChannel.read(byteBuffer);
   ```

   `read(byteBuffer)`方法会阻塞线程直到接收到新消息

[示例代码#serverSocketChannelBlockingTest](./netty_demo/src/main/test/top/ersut/SocketChannelTest.java)：

```java
public void serverSocketChannelBlockingTest() throws IOException {

    //1、打开服务端
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

    //2、绑定端口
    serverSocketChannel.bind(new InetSocketAddress(6666));
    log.debug("serverSocketChannel start...");

    //用来存放连接的客户端
    List<SocketChannel> clientSocketChannels = new ArrayList<>();

    while (true) {
        //3、与客户端建立连接。并返回一个 SocketChannel 用来与客户端通信；accept()会阻塞
        SocketChannel socketChannel = serverSocketChannel.accept();
        log.debug("remotePort:[{}],connected...",((InetSocketAddress)socketChannel.getRemoteAddress()).getPort());
        //新来的客户端放入客户端列表
        clientSocketChannels.add(socketChannel);
        //遍历客户端列表，接收客户端消息
        for (SocketChannel clientSocketChannel : clientSocketChannels) {
            int port = ((InetSocketAddress) clientSocketChannel.getRemoteAddress()).getPort();
            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
            //4、获取客户端发来的消息，此处也会阻塞
            clientSocketChannel.read(byteBuffer);
            log.debug("remotePort:[{}],read...", port);
            byteBuffer.flip();

            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
            log.debug("remotePort:[{}],message:[{}]", port,charBuffer);

            //清除,准备接收下一次消息
            byteBuffer.clear();
        }
    }
}
```

与客户端连接后运行结果：

```txt
23:28:24.655 [main] DEBUG top.ersut.SocketChannelTest - serverSocketChannel start...
23:28:31.044 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[65009],connected...
23:28:31.047 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[65009],read...
23:28:31.048 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[65009],message:[hello!]
23:28:34.047 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[65014],connected...
23:28:34.048 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[65009],read...
23:28:34.049 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[65009],message:[hello2!]
23:28:34.050 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[65014],read...
23:28:34.052 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[65014],message:[hi!]
```

通过日志可以看到客户端发送的`hi2!`没有接收到。

##### 4.1.2.2 分析客户端与阻塞服务端的代码

1. 服务端阻塞在`accept()`方法；
2. **客户端的`client1`（即日志中的`remotePort:[65009]`）连接服务端，`accept()`方法本次阻塞结束；**
3. 服务端与`client1`的链接进行`clientSocketChannel.read(byteBuffer);`阻塞了；
4. 客户端的`client1`发送"hello!"消息，`clientSocketChannel.read(byteBuffer);`结束阻塞并成功接收"hello!"消息；
5. **客户端的`client1`发送"hello2!"消息；**
6. 服务端进入下次while循环；
7. 服务端循环第二次阻塞在了`accept()`方法；
8. **客户端的`client2`（即日志中的`remotePort:[65014]`）连接服务端，`accept()`方法本次阻塞结束；**
9. **服务端与`client1`的链接进行`clientSocketChannel.read(byteBuffer);`接收了"hello2!"消息；**
10. 服务端与`client2`的链接进行`clientSocketChannel.read(byteBuffer);`阻塞了；
11. 客户端的`client2`发送"hi!"消息，`clientSocketChannel.read(byteBuffer);`结束阻塞并成功接收"hi!"消息；
12. **客户端的`client2`发送"hi2!"消息；**
13. 服务端进入下次while循环；
14. **服务端循环第三次阻塞在了`accept()`方法；由于没有第三个客户端进行连接所以无法执行到`clientSocketChannel.read(byteBuffer);`，也就无法接收`client2`发送的"hi2!"消息；**

##### 4.1.2.3 ⚠️阻塞服务端的缺点

1. **单线程下**，无法兼顾接收客户端消息和连接新客户端
2. **多线程下**，32 位 jvm 一个线程 320k，64 位 jvm 一个线程 1024k，如果连接数过多会导致 OOM，并且线程太多，反而会因为cpu频繁切换线程导致性能降低
3. 线程池方案下，可以减少线程数和线程切换次数，但是建立很多连接会导致阻塞后边的线程，这种方案适合短连接，不适合长连接，所以治表不治本。

#### 4.1.3 非阻塞的服务端

##### 4.1.3.1 创建非阻塞的客户端

1. 打开服务端

   ```java
   ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
   ```

2. 绑定端口

   ```java
   serverSocketChannel.bind(new InetSocketAddress(6666));
   ```

3. **设置服务端为非阻塞模式**

   ```java
   serverSocketChannel.configureBlocking(false);
   ```

   默认是true，即阻塞模式

4. 等待客户端

   ```java
   SocketChannel socketChannel = serverSocketChannel.accept();
   ```

   由于ServerSocketChannel配置了非阻塞模式，此处不会阻塞，没有新客户端时返回null

5. **设置设置与客户端的连接为非阻塞模式**

   ```java
   socketChannel.configureBlocking(false);
   ```

   默认是true，即阻塞模式

6. 接收客户端的消息

   ```java
   ByteBuffer byteBuffer = ByteBuffer.allocate(16);
   socketChannel.read(byteBuffer);
   ```

   获取客户端发来的消息，由于SocketChannel设置了非阻塞此处不再阻塞，没有消息时byteBuffer中不存在有效数据

[示例代码#serverSocketChannelNonBlockingTest](./netty_demo/src/main/test/top/ersut/SocketChannelTest.java)：

```java
//1、创建服务端
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

//2、绑定端口
serverSocketChannel.bind(new InetSocketAddress(6666));
log.debug("serverSocketChannel start...");

//3、设置服务端为非阻塞模式
serverSocketChannel.configureBlocking(false);

//用来存放连接的客户端
List<SocketChannel> clientSocketChannels = new ArrayList<>();

while (true) {
    //4、与客户端建立连接。并返回一个 SocketChannel 用来与客户端通信
    //由于ServerSocketChannel配置了非阻塞模式，此处不会阻塞，没有新客户端时返回null
    SocketChannel socketChannel = serverSocketChannel.accept();

    //判断是否有新客户端
    if(socketChannel != null){
        log.debug("remotePort:[{}],connected...",((InetSocketAddress)socketChannel.getRemoteAddress()).getPort());

        //5、设置与客户端的连接为非阻塞模式
        socketChannel.configureBlocking(false);

        //新来的客户端放入客户端列表
        clientSocketChannels.add(socketChannel);
    }

    //遍历客户端列表，接收客户端消息
    for (SocketChannel clientSocketChannel : clientSocketChannels) {
        int port = ((InetSocketAddress) clientSocketChannel.getRemoteAddress()).getPort();
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //6、获取客户端发来的消息，由于SocketChannel设置了非阻塞此处不再阻塞，没有消息时byteBuffer中不存在有效数据
        clientSocketChannel.read(byteBuffer);

        //判断是否读取到数据
        if(byteBuffer.position() > 0){
            log.debug("remotePort:[{}],read...", port);
            byteBuffer.flip();

            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
            log.debug("remotePort:[{}],message:[{}]", port,charBuffer);

            //清除,准备接收下一次消息
            byteBuffer.clear();
        }
    }
}
```

与客户端连接后运行结果：

```tex
08:21:32.868 [main] DEBUG top.ersut.SocketChannelTest - serverSocketChannel start...
08:21:43.578 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[58374],connected...
08:21:43.582 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[58374],read...
08:21:43.582 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[58374],message:[hello!]
08:21:46.583 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[58374],read...
08:21:46.584 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[58374],message:[hello2!]
08:21:46.584 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[58382],connected...
08:21:46.584 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[58382],read...
08:21:46.584 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[58382],message:[hi!]
08:21:49.584 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[58382],read...
08:21:49.584 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[58382],message:[hi2!]
```

没有了阻塞所有消息接收正常

##### 4.1.3.2 ⚠️非阻塞服务端的缺点

1. 非阻塞解决了非阻塞的几个问题，但是带来的新的问题，由于没有了阻塞while循环一直在运行导致浪费cpu的资源

**使用`Selector`可以解决这个痛点**

### 4.2 Selector

优点：

- 通过Selector，在单线程下可以管理多个channel（SocketChannel、ServerSocketChannel）
  - 因为是单线程也解决了**多线程的占用内存资源**和**线程太多频繁切换**的问题
- Selector会阻塞线程直到有管理的channel有事件发生，解决了浪费cpu资源的问题

#### 4.2.1 Selector监听事件

##### 1、创建Selector

```java
Selector selector = Selector.open();
```

##### 2、注册事件到channel

```java
serverSocketChannel.configureBlocking(false);
SelectionKey selectionKeyByServer = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
```

💡 **channel 必须工作在非阻塞模式**，FileChannel 没有非阻塞模式，因此不能配合 selector 一起使用

register方法：channel中注册Selector

- 参数1：Selector
- 参数2：监听的事件类型
  - SelectionKey#OP_ACCEPT：服务端有新客户端连接的事件
  - SelectionKey#OP_CONNECT：客户端与服务端连接成功的事件
  - SelectionKey#OP_READ：可读事件，异常断开、正常关闭也会调用这里，如果发送的数据大于 buffer 缓冲区，会触发多次读取事件
  - SelectionKey#OP_WRITE：可写事件

**注意：其他channel还可以绑定到这个Selector中进行管理**

##### 3、阻塞线程等待监听的事件

```java
int count = selector.select();
```

##### 4、获取监听到的事件

```java
Set<SelectionKey> selectionKeys = selector.selectedKeys();
```

💡一些观察

注册事件的返回值是`SelectionKey`类型，获取监听事件的类型是`Set<SelectionKey>`，众所周知`Set`是不重复的集合，那么也就是说，一个`Channel`中同一个事件，即使同一时间触发了两次，`selector.selectedKeys()`也要运行两次来接收。



示例代码：

```java

try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
     //1、创建 Selector
     Selector selector = Selector.open();) {

    serverSocketChannel.bind(new InetSocketAddress(6666));
    log.debug("serverSocketChannel start...");

    //只有非阻塞的channel才能使用Selector
    serverSocketChannel.configureBlocking(false);
    //2、服务端中注册selector，并监听 accept 事件
    //register方法的参数1 Selector；参数2 监听的事件类型
    SelectionKey selectionKeyByServer = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    while (true) {
        //3、等待触发监听的事件，这里会阻塞
        int count = selector.select();

        //4、获取监听到的事件
        Set<SelectionKey> selectionKeys = selector.selectedKeys();

        //遍历所有事件
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()){
            SelectionKey selectionKey = iterator.next();
            log.debug("selectionKey:[{}]",selectionKey);
        }
    }
}
```

### 4.3 事件的处理

```java
//3、等待触发监听的事件，这里会阻塞
int count = selector.select();

//4、获取监听到的事件
Set<SelectionKey> selectionKeys = selector.selectedKeys();

//遍历所有事件
Iterator<SelectionKey> iterator = selectionKeys.iterator();
while (iterator.hasNext()){
    SelectionKey selectionKey = iterator.next();
    log.debug("selectionKey:[{}]",selectionKey);
    //判断事件类型
    if(selectionKey.isAcceptable()){
        //处理事件
        ServerSocketChannel channel = (ServerSocketChannel)selectionKey.channel();
        SocketChannel socketChannel = channel.accept();
        log.debug("remotePort:[{}],connected...",((InetSocketAddress)socketChannel.getRemoteAddress()).getPort());
    }
    //事件处理后移除，否则该事件还会进入下一轮循环
    iterator.remove();
}
```

`selectionKey.channel()`获取事件对应的channel。

#####  ⚠️事件必须处理

不处理`select`方法不会阻塞，要么处理，要么退出(`selectionKey.cancel()`，即不再监听对应channel的这个事件)

#### 4.3.1 OP_ACCEPT事件

通过`selectionKey.isAcceptable()`判断事件是否处理

示例代码：

```java
while (iterator.hasNext()){
    SelectionKey selectionKey = iterator.next();
    log.debug("selectionKey:[{}]",selectionKey);
	//判断事件类型
    if(selectionKey.isAcceptable()){
        //处理事件
        ServerSocketChannel channel = (ServerSocketChannel)selectionKey.channel();
        SocketChannel socketChannel = channel.accept();
        log.debug("remotePort:[{}],connected...",((InetSocketAddress)socketChannel.getRemoteAddress()).getPort());
    }
    //事件处理后移除，否则该事件还会进入下一轮循环
    iterator.remove();
}
```

#### 4.3.2 OP_READ可读事件

通过`selectionKey.isReadable()`判断事件是否处理

**需要在新客户端连接上来的时候注册这个事件**

```java
while (iterator.hasNext()){
    SelectionKey selectionKey = iterator.next();
    log.debug("selectionKey:[{}]",selectionKey);
    if(selectionKey.isAcceptable()){
        ServerSocketChannel channel = (ServerSocketChannel)selectionKey.channel();
        SocketChannel socketChannel = channel.accept();
        log.debug("remotePort:[{}],connected...",((InetSocketAddress)socketChannel.getRemoteAddress()).getPort());

        socketChannel.configureBlocking(false);
        //注册 read 事件
        socketChannel.register(selector,SelectionKey.OP_READ);
    } else if(selectionKey.isReadable()){
        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
        int port = ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort();

        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        socketChannel.read(byteBuffer);
        log.debug("remotePort:[{}],read...",port);

        byteBuffer.flip();
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
        log.debug("remotePort:[{}],message:[{}]", port,charBuffer);
        byteBuffer.clear();
    }
    //事件处理后移除，否则该事件还会进入下一轮循环
    iterator.remove();
}
```

**注意**：使用`Selector`的`Channel`必须是非阻塞的，即本实例中的`socketChannel.configureBlocking(false);`

#####  **⚠️事件处理后要手动移除**

`selector.selectedKeys()`中的Set集合，**在处理事件后系统不会自动移除**，那这样当我们执行了`channel.accept()`后，**下次其他事件进入**，还是会执行`channel.accept()`可是这时候返回的是null，所以要对`selectedKeys`集合进行移除。

问题复现：服务端去掉`iterator.remove();`，客户端连接服务端，再发送一条消息。

1. 第一次`accept`事件进入处理后继续阻塞
2. 第二次`read`事件进入
3. `if(selectionKey.isAcceptable())`判断还是可以通过
4. 执行`SocketChannel socketChannel = channel.accept()`
5.  `socketChannel `为null，下边的代码就要报空指针了

##### 处理客户端的正常关闭和强制关闭

不处理会导致的问题：

- 正常关闭：客户端会发送一条结束消息，这条消息不包含数据，那么服务端读取不到数据，所以`Selector`就认为没有处理这个事件，导致进入了死循环。正确的处理方式是服务端关闭这个channel并退出Selector。
- 强制关闭：服务端会报异常：`java.io.IOException: 远程主机强迫关闭了一个现有的连接。`

部分代码：

```java
...
//等待触发监听的事件，这里会阻塞
int count = selector.select();

//获取监听到的事件
Set<SelectionKey> selectionKeys = selector.selectedKeys();

//遍历所有事件
Iterator<SelectionKey> iterator = selectionKeys.iterator();
while (iterator.hasNext()){
    SelectionKey selectionKey = iterator.next();
    if(selectionKey.isReadable()){
        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
        int port = ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort();

        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        try {
            int len = socketChannel.read(byteBuffer);
            //-1代表客户端正常关闭
            if(len == -1){
                log.debug("remotePort:[{}],close...",port);
                //退出Selector
                selectionKey.cancel();
                socketChannel.close();
                continue;
            }
        } catch (IOException e){
            //处理客户端 强制关闭 的情况
            log.debug("remotePort:[{}],error...",port);
            //退出Selector
            selectionKey.cancel();
            socketChannel.close();
            continue;
        }
        log.debug("remotePort:[{}],read...",port);

        byteBuffer.flip();
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
        log.debug("remotePort:[{}],message:[{}]", port,charBuffer);
        byteBuffer.clear();
    }
}
...
```

[示例代码#SelectorAcceptAndReadTest](./netty_demo/src/main/test/top/ersut/SocketChannelTest.java)

##### 消息边界的处理

为什么要处理消息边界

![](images/0023.png)



客户端发送消息时，可能是一次发送多条消息（粘包），也看可能一条消息太大分多次发送。这就涉及到了拆包，拆包就是根据消息边界来拆分的。



##### 💡消息边界的解决方案

1. 通过分割符来标识一条消息的结束，缺点是效率低（因为要遍历每个字节来查找分割符）

2. 通过固定长度的消息来区分每条消息，若实际消息小于长度，用占位符补齐，缺点是浪费带宽且不灵活

3. TLV编码

   - T：Type消息类型
   - L：Length消息的长度
   - V：Value数据，数据的长度是由 L 指定的

   优点：消息的长度已知的情况下，可以分配buffer的大小

   缺点：消息过大时，影响服务端的吞吐量；由于buffer放在内存中，大消息且高并发的情况下，占用内存过多



了解以下方法，让**消息可以连续**。

创建**selectionKey的附件**

```java
ByteBuffer byteBuffer = ByteBuffer.allocate(8);
socketChannel.register(selector,SelectionKey.OP_READ,byteBuffer);
```

参数：

1. 选择器
2. 事件
3. selectionKey的附件（可以是任意对象）

**selectionKey附件的使用**

```java
ByteBuffer byteBuffer = (ByteBuffer)selectionKey.attachment();
```

`attachment()`方法是获取selectionKey中绑定的附件

```java
ByteBuffer newByteBuffer = ByteBuffer.allocate(16);
newByteBuffer.put(byteBuffer);
selectionKey.attach(newByteBuffer);
```

`attach()`方法是更新selectionKey中的附件

**通过附件可以实现同一个selectionKey中触发多次事件，让后续的事件获取上一个事件留下的内容，从而产生关联**

消息边界的解决方案1：

时序图

```mermaid
sequenceDiagram
participant c as 客户端
participant s as 服务端
participant b as bytebuffer(8)
participant b2 as bytebuffer(16)
c->>s:第一次发送消息：01234567
s->>b:存储到buffer
b->>s:未遍历到分割符
s->>b2:扩容
b->>b2:拷贝
c ->> s:第二次发送消息：89ABCDE\n
s->> b2:存储到buffer
b2->>s:遍历找到分割符\n



```

代码实现

[服务端#messageBoundaryTest](./netty_demo/src/main/test/top/ersut/SocketChannelTest.java)：

```java
/**
 * 解析数据
 * @param souce
 */
private byte[] split(ByteBuffer souce){
    byte[] msg = null;
    //改为读模式，并获取limit
    int len = souce.flip().limit();
    for (int i = 0; i < len; i++) {
        byte currByte = souce.get(i);
        //如果有\n 代表有消息；
        if(currByte == '\n'){
            msg = new byte[i+1];
            for (int j = 0; j <= i; j++) {
                msg[j] = souce.get();
            }
            break;
        }
    }
    //如果没有分割符，那么执行compact后，position和limit相等
    souce.compact();
    return msg;
}

/**
 * 处理消息边界
 * 每条消息以\n结尾
 */
@Test
public void messageBoundaryTest() {
    try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
         //创建 Selector
         Selector selector = Selector.open();) {

        serverSocketChannel.bind(new InetSocketAddress(6667));
        log.debug("serverSocketChannel start...");

        //只有非阻塞的channel才能使用Selector
        serverSocketChannel.configureBlocking(false);
        //服务端中注册selector，并监听 accept 事件
        //register方法的参数1 Selector；参数2 监听的事件类型
        SelectionKey selectionKeyByServer = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            //等待触发监听的事件，这里会阻塞
            int count = selector.select();

            //获取监听到的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //遍历所有事件
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if(selectionKey.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel)selectionKey.channel();
                    SocketChannel socketChannel = channel.accept();

                    socketChannel.configureBlocking(false);
                    ByteBuffer byteBuffer = ByteBuffer.allocate(8);
                    //注册 read 事件 , 添加第三个参数：注册一个SelectionKey级的变量
                    socketChannel.register(selector,SelectionKey.OP_READ,byteBuffer);
                } else if(selectionKey.isReadable()){
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                    int port = ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort();

                    //获取在SelectionKey中存入的变量
                    ByteBuffer byteBuffer = (ByteBuffer)selectionKey.attachment();
                    try {
                        int len = socketChannel.read(byteBuffer);
                        log.debug("remotePort:[{}],read...",port);
                        //-1代表客户端关闭
                        if(len == -1){
                            log.debug("remotePort:[{}],close...",port);
                            //退出Selector
                            selectionKey.cancel();
                            socketChannel.close();
                            continue;
                        }
                    } catch (IOException e){
                        //处理客户端强制关闭的情况
                        log.debug("remotePort:[{}],error...",port);
                        //退出Selector
                        selectionKey.cancel();
                        socketChannel.close();
                        continue;
                    }
                    byte[] message;
                    //循环，处理一次读取中多条信息的情况。
                    do {
                        message = split(byteBuffer);
                        //是否需要扩容
                        if(byteBuffer.position() == byteBuffer.limit()){
                            byteBuffer.flip();
                            //没有分割符 扩容
                            int size = byteBuffer.capacity() * 2;
                            log.debug("remotePort:[{}],dilatation:[{}]", port,size);
                            ByteBuffer newByteBuffer = ByteBuffer.allocate(size);
                            newByteBuffer.put(byteBuffer);
                            selectionKey.attach(newByteBuffer);
                            break;
                        }
                        //判断是否有消息
                        if (message != null){
                            String str = new String(message, StandardCharsets.UTF_8);
                            log.debug("remotePort:[{}],message:[{}]", port,str);
                        }
                    }while (message != null);
                }
            }
            //事件处理后移除，否则该事件还会进入下一轮循环
            iterator.remove();
        }
    } catch (IOException e){
        log.error("",e);
    }
}
```

[客户端#messageBoundaryClientTest](./netty_demo/src/main/test/top/ersut/SocketChannelTest.java)：

```java
public void messageBoundaryClientTest(){
    try (SocketChannel socketChannel = SocketChannel.open();){
        socketChannel.connect(new InetSocketAddress(6667));
        socketChannel.write(StandardCharsets.UTF_8.encode("1949年，"));
        socketChannel.write(StandardCharsets.UTF_8.encode("中华人民共和国\n"));
        socketChannel.write(StandardCharsets.UTF_8.encode("成立啦！\n牛逼!\n"));
        Thread.sleep(3*1000);
    } catch (IOException e) {
        e.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

服务端打印：

```tex
10:39:54.853 [main] DEBUG top.ersut.SocketChannelTest - serverSocketChannel start...
10:39:59.476 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],read...
10:39:59.479 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],dilatation:[16]
10:39:59.479 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],read...
10:39:59.479 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],dilatation:[32]
10:39:59.479 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],read...
10:39:59.480 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],message:[1949年，中华人民共和国
]
10:39:59.480 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],read...
10:39:59.480 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],message:[成立啦！
]
10:39:59.480 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],message:[牛逼!
]
10:40:02.477 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],read...
10:40:02.477 [main] DEBUG top.ersut.SocketChannelTest - remotePort:[64429],close...
```

#### 4.3.3 OP_WRITE可写事件

发送消息的方法：`SocketChannel.write(ByteBuffer byteBuffer)`

**为什么需要可写事件？明明通过write方法就可以直接写入的！**

write方法不能保证一次把`ByteBuffer `中的数据全部发送出去，所以要使用循环直到数据发送完成，但是循环发送数据不是每一次都能发送，那么循环会出现空转的情况，造成浪费cpu、占用线程的情况。通过可写事件进行写入，可以解决这个问题，**因为可写事件只有在可以写入时触发**。

问题复现代码：

```java
try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
     //创建 Selector
     Selector selector = Selector.open();) {
    serverSocketChannel.bind(new InetSocketAddress(writePort));
    log.debug("serverSocketChannel start...");
    serverSocketChannel.configureBlocking(false);
    SelectionKey selectionKeyByServer = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    while (true) {
        selector.select();

        //获取监听到的事件
        Set<SelectionKey> selectionKeys = selector.selectedKeys();

        //遍历所有事件
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()){
            SelectionKey selectionKey = iterator.next();
            if(selectionKey.isAcceptable()){
                ServerSocketChannel channel = (ServerSocketChannel)selectionKey.channel();
                SocketChannel socketChannel = channel.accept();

                StringBuilder sendMessage = new StringBuilder();
                for (int i = 0; i < 9999999; i++) {
                    sendMessage.append("a");
                }
                ByteBuffer sendByteBuffer = StandardCharsets.UTF_8.encode(sendMessage.toString());
                //方式一:循环发送消息，低效率发送，会出现空转情况
                while (sendByteBuffer.hasRemaining()){
                    int write = socketChannel.write(sendByteBuffer);
                    log.info("发送数据长度:[{}]",write);
                }
            }
        }
        //事件处理后移除，否则该事件还会进入下一轮循环
        iterator.remove();
    }
} catch (IOException e){
    log.error("",e);
}
```

[客户端#writeClientTest](./netty_demo/src/main/test/top/ersut/SocketChannelTest.java)：

```java
public void writeClientTest(){
    try (SocketChannel socketChannel = SocketChannel.open();){
        socketChannel.connect(new InetSocketAddress(writePort));

        int countLen = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024);
        while (true){
            countLen += socketChannel.read(byteBuffer);
            log.info("已接收字节[{}]",countLen);
            byteBuffer.clear();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

部分打印信息：

```tex
23:13:16.525 [main] DEBUG top.ersut.SocketChannelTest - serverSocketChannel start...
23:13:20.027 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[3014633]
23:13:20.031 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[0]
23:13:20.033 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[0]
23:13:20.035 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[0]
23:13:20.051 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[4718556]
23:13:20.052 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[0]
23:13:20.053 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[655355]
23:13:20.058 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[1611455]
```

**通过上边的日志，可以发现有些循环并没有发送数据，造成循环空转**

##### 使用可写事件的代码：

[服务端#writeTest](./netty_demo/src/main/test/top/ersut/SocketChannelTest.java)：

```java
public void writeTest() {
    try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
         //创建 Selector
         Selector selector = Selector.open();) {
        serverSocketChannel.bind(new InetSocketAddress(writePort));
        log.debug("serverSocketChannel start...");
        serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKeyByServer = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();

            //获取监听到的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //遍历所有事件
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if(selectionKey.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel)selectionKey.channel();
                    SocketChannel socketChannel = channel.accept();

                    socketChannel.configureBlocking(false);
                    //订阅多个事件，将值相加即可
                    SelectionKey clientSelectionKey = socketChannel.register(selector, 0);

                    StringBuilder sendMessage = new StringBuilder();
                    for (int i = 0; i < 9999999; i++) {
                        sendMessage.append("a");
                    }
                    ByteBuffer sendByteBuffer = StandardCharsets.UTF_8.encode(sendMessage.toString());

                    //方式二：通过可写事件发送消息
                    //判断是否有数据
                    if(sendByteBuffer.hasRemaining()){
                        //在selectionKey中关注新事件时，如果要保留旧事件，获取旧事件进行相加即可
                        clientSelectionKey.interestOps(clientSelectionKey.interestOps()+SelectionKey.OP_WRITE);
                        clientSelectionKey.attach(sendByteBuffer);
                    }
                } else if(selectionKey.isWritable()){
                    /*
                    为什么需要可写事件，明明发送数据可以循环直到数据发送完
                    因为循环发送数据不是每一次都能发送，那么循环会出现空转的情况，造成浪费cpu、占用线程的情况。所以通过可写事件进行写入，可以解决这个问题。
                     */
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                    ByteBuffer sendByteBuffer = (ByteBuffer) selectionKey.attachment();
                    //发送数据
                    int write = socketChannel.write(sendByteBuffer);
                    log.info("发送数据长度:[{}]",write);
                    //判断是否发送完
                    if(!sendByteBuffer.hasRemaining()){
                        //数据发送完成，取消可写事件
                        selectionKey.interestOps(selectionKey.interestOps()-SelectionKey.OP_WRITE);
                        //释放附件
                        selectionKey.attach(null);
                    }
                }
            }
            //事件处理后移除，否则该事件还会进入下一轮循环
            iterator.remove();
        }
    } catch (IOException e){
        log.error("",e);
    }
}
```

打印信息：

```tex
23:16:04.290 [main] DEBUG top.ersut.SocketChannelTest - serverSocketChannel start...
23:17:05.213 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[3014633]
23:17:05.229 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[3014633]
23:17:05.234 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[3276775]
23:17:05.235 [main] INFO top.ersut.SocketChannelTest - 发送数据长度:[693958]
```

##### 💡 write 为何要取消

只要向 channel 发送数据时，socket 缓冲可写，这个事件会频繁触发，因此应当只在 socket 缓冲区写不下时再关注可写事件，数据写完之后再取消关注

### 4.4  提高性能：Selector的多线程

#### 4.4.1  唤醒阻塞的Selector

**wakeup()方法**

调用`wakeup()`后，如果当前阻塞，那么结束这次阻塞；如果当前未阻塞，那么下一次不阻塞

#### 4.4.2 多线程方案

假如CPU有4核（同时可以处理4个线程），像以往的代码只使用单线程，那CPU的资源就浪费了，使用多线程处理这样充分利用cpu的资源

方案：一个线程负责处理ServerSocketChannel的事件，多个（CPU的核心数）线程负责SocketChannel的事件



```mermaid
sequenceDiagram
participant c as 客户端
participant t1 as Thread(boss)
participant t2 as Thread(worker)

t1->>t1:等待客户端连接
c->>t1:连接服务端
t1->>t2:发现客户端，交给worker处理
t2->>t2:等待客户端消息
c->>t2:发送消息
t2->>t2:接收消息并处理
```

[该方案代码](./netty_demo/src/main/test/top/ersut/MultipleSelectorTest.java)：

```java
int multipleServerSocketPort = 8615;

@Test
public void MultipleSelectorTest(){
    Thread.currentThread().setName("boss");

    AtomicInteger index = new AtomicInteger();
    //cpu核心数,建议通过配置文件调整，因为docker读取的核心数是物理主机的，不是容器真实的核心数，所以会导致获取错误
    int cpuCore = 4;
    WorkerLoop[] workerLoops = new WorkerLoop[cpuCore];
    for (int i = 0; i < workerLoops.length; i++) {
        WorkerLoop workerLoop = new WorkerLoop(i);
        workerLoops[i] = workerLoop;
    }
    try (
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open();
            ){
        serverSocketChannel.bind(new InetSocketAddress(multipleServerSocketPort));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("serverSocketChannel start...");

        while (true){
            selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()){
                SelectionKey selectionKey = keyIterator.next();
                if (selectionKey.isAcceptable()){
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    log.info("new client:[{}]",socketChannel.getRemoteAddress());
                    workerLoops[index.getAndIncrement() % workerLoops.length].register(socketChannel);
                }
                keyIterator.remove();
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        for (int i = 0; i < workerLoops.length; i++) {
            workerLoops[i].close();
        }
    }
}

class WorkerLoop implements Runnable{
    /**
     * 线程对象
     */
    private Thread thread;
    /**
     * SocketChannel的选择器（多路复用）
     * 负责管理该线程的SocketChannel
     */
    private Selector worker;
    //索引，线程名使用
    private int index;
    //标识当前线程是否启动
    private volatile boolean starting = false;
    //任务队列，用于俩个线程之间的交互
    ConcurrentLinkedQueue<Runnable> registerTasks = new ConcurrentLinkedQueue<>();

    public void init() throws IOException {
        if(!starting){
            worker = Selector.open();
            thread = new Thread(this,"worker- "+index);
            thread.start();
            starting = true;
        }
    }

    public void register(SocketChannel socketChannel) throws IOException {
        init();
        registerTasks.add(() -> {
            try {
                socketChannel.register(this.worker,SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        });
        //结束Selector的阻塞，若当前阻塞中，那么结束阻塞；若当前未阻塞，那么下一次不阻塞
        worker.wakeup();
    }

    public WorkerLoop(int index) {
        this.index = index;
    }

    public void close(){
        if (worker.isOpen()){
            try {
                worker.close();
            } catch (IOException e) {
                log.error("worker close error",e);
            }
        }
    }


    @SneakyThrows
    @Override
    public void run() {
        while (worker.isOpen()){
            worker.select();
            //处理任务
            Runnable task = registerTasks.poll();
            //执行所有任务
            while (task != null) {
                task.run();
                //获取任务
                task = registerTasks.poll();
            }
            Iterator<SelectionKey> keyIterator = worker.selectedKeys().iterator();
            while (keyIterator.hasNext()){
                SelectionKey selectionKey = keyIterator.next();
                if (selectionKey.isReadable()){
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                    int port = ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort();

                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    try {
                        int len = socketChannel.read(byteBuffer);
                        //-1代表客户端关闭
                        if(len == -1){
                            log.debug("remotePort:[{}],close...",port);
                            //退出Selector
                            selectionKey.cancel();
                            socketChannel.close();
                            continue;
                        }
                    } catch (IOException e){
                        //处理客户端强制关闭的情况
                        log.debug("remotePort:[{}],error...",port);
                        //退出Selector
                        selectionKey.cancel();
                        socketChannel.close();
                        continue;
                    }
                    log.debug("remotePort:[{}],read...",port);

                    byteBuffer.flip();
                    CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
                    log.debug("remotePort:[{}],message:[{}]", port,charBuffer);
                    byteBuffer.clear();
                }
                keyIterator.remove();
            }
        }
    }
}
```

#### 4.4.3 需要注意的地方

- cpu的核心数不要通过java获取，因为**在docker容器中java获取的是宿主机的核心数**，而容器并未分配那么多核心数，对应的解决方案是通过配置文件进行配置
- 因为Selector是阻塞的，所以boss线程中不要使用worker的Selector，假如使用了会导致主线程也阻塞！解决方案：通过线程安全的队列（ConcurrentLinkedQueue）进行线程之间的交互

## 5.零拷贝

> 零拷贝笔记来源黑马课程（B站 BV1py4y1E7oA）

#### 传统 IO 问题

传统的 IO 将一个文件通过 socket 写出

```java
File f = new File("helloword/data.txt");
RandomAccessFile file = new RandomAccessFile(file, "r");

byte[] buf = new byte[(int)f.length()];
file.read(buf);

Socket socket = ...;
socket.getOutputStream().write(buf);
```

内部工作流程是这样的：

![](./images/0024.png)

1. java 本身并不具备 IO 读写能力，因此 read 方法调用后，要从 java 程序的**用户态**切换至**内核态**，去调用操作系统（Kernel）的读能力，将数据读入**内核缓冲区**。这期间用户线程阻塞，操作系统使用 DMA（Direct Memory Access）来实现文件读，其间也不会使用 cpu

   > DMA 也可以理解为硬件单元，用来解放 cpu 完成文件 IO

2. 从**内核态**切换回**用户态**，将数据从**内核缓冲区**读入**用户缓冲区**（即 byte[] buf），这期间 cpu 会参与拷贝，无法利用 DMA

3. 调用 write 方法，这时将数据从**用户缓冲区**（byte[] buf）写入 **socket 缓冲区**，cpu 会参与拷贝

4. 接下来要向网卡写数据，这项能力 java 又不具备，因此又得从**用户态**切换至**内核态**，调用操作系统的写能力，使用 DMA 将 **socket 缓冲区**的数据写入网卡，不会使用 cpu

​	

可以看到中间环节较多，java 的 IO 实际不是物理设备级别的读写，而是缓存的复制，底层的真正读写是操作系统来完成的

* 用户态与内核态的切换发生了 3 次，这个操作比较重量级
* 数据拷贝了共 4 次



#### NIO 优化

通过 DirectByteBuf 

* ByteBuffer.allocate(10)  HeapByteBuffer 使用的还是 java 内存
* ByteBuffer.allocateDirect(10)  DirectByteBuffer 使用的是操作系统内存

![](./images/0025.png)

大部分步骤与优化前相同，不再赘述。唯有一点：java 可以使用 DirectByteBuf 将堆外内存映射到 jvm 内存中来直接访问使用

* 这块内存不受 jvm 垃圾回收的影响，因此内存地址固定，有助于 IO 读写
* java 中的 DirectByteBuf 对象仅维护了此内存的虚引用，内存回收分成两步
  * DirectByteBuf 对象被垃圾回收，将虚引用加入引用队列
  * 通过专门线程访问引用队列，根据虚引用释放堆外内存
* 减少了一次数据拷贝，用户态与内核态的切换次数没有减少



进一步优化（底层采用了 linux 2.1 后提供的 sendFile 方法），java 中对应着两个 channel 调用 transferTo/transferFrom 方法拷贝数据

![](./images/0026.png)

1. java 调用 transferTo 方法后，要从 java 程序的**用户态**切换至**内核态**，使用 DMA将数据读入**内核缓冲区**，不会使用 cpu
2. 数据从**内核缓冲区**传输到 **socket 缓冲区**，cpu 会参与拷贝
3. 最后使用 DMA 将 **socket 缓冲区**的数据写入网卡，不会使用 cpu

可以看到

* 只发生了一次用户态与内核态的切换
* 数据拷贝了 3 次



进一步优化（linux 2.4）

![](./images/0027.png)

1. java 调用 transferTo 方法后，要从 java 程序的**用户态**切换至**内核态**，使用 DMA将数据读入**内核缓冲区**，不会使用 cpu
2. 只会将一些 offset 和 length 信息拷入 **socket 缓冲区**，几乎无消耗
3. 使用 DMA 将 **内核缓冲区**的数据写入网卡，不会使用 cpu

整个过程仅只发生了一次用户态与内核态的切换，数据拷贝了 2 次。所谓的【零拷贝】，并不是真正无拷贝，而是在不会拷贝重复数据到 jvm 内存中，零拷贝的优点有

* 更少的用户态与内核态的切换
* 不利用 cpu 计算，减少 cpu 缓存伪共享
* 零拷贝适合小文件传输
