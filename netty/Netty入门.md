# Netty入门



## 1. 概述

### 1.1 Netty是什么

Netty是一个**异步的、基于事件驱动的**网络应用框架，用于快速开发**可维护、高性能**的网络服务器和客户端。

Netty是基于NIO实现的，上段话中“基于事件驱动的”指的是Selector的事件。

### 1.2 Netty的地位

Netty在Java网络应用框架的地位好比spring在JavaEE中的地位

以下框架都是使用了Netty，因为他们都有网络通信需求

* gRPC - rpc 框架
* Dubbo - rpc 框架
* Spring 5.x - flux api 完全抛弃了 tomcat ，使用 netty 作为服务器端
* Cassandra - nosql 数据库
* Spark - 大数据分布式计算框架
* Hadoop - 大数据分布式存储框架
* RocketMQ - ali 开源的消息队列
* ElasticSearch - 搜索引擎
* Zookeeper - 分布式协调框架

### 1.3 Netty的优势

- Netty vs NIO
  - NIO工作量大，Bug多
  - 需要自己构建协议
  - 解决TCP传输数据中的问题，例如：粘包、半包、拆包
  - NIO在Linux下，epoll 空轮询导致 CPU 100%的问题
  - 对API进行了增强，更易用，例如：FastThreadLocal => ThreadLocal，ByteBuf => ByteBuffer
- Netty vs 其他框架
  - Mina 由 Apache 维护，与 Netty 相比 Netty 的迭代速度更快，API更简洁。
  - 经久考验，Netty 诞生与2004年，至今已有19年的时间，
    - 2.x版本 2004年
    - 3.x版本 2008年
    - 4.x版本 2013年
    - 5.x版本 已废弃，没有明显的性能提升，且维护成本高

## 2. Hello Netty 

###  2.1 需求

- 服务端接收消息并打印
- 客户端向服务端发送 hello netty

引入依赖：

```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.92.Final</version>
</dependency>
```

### 2.2 服务端

```java
//netty的服务端启动器
new ServerBootstrap()
    //1、配置事件轮询组，NioEventLoopGroup: selector的多线程模式
    .group(new NioEventLoopGroup())
    //2、配置服务端的通道
    .channel(NioServerSocketChannel.class)
    //3、配置与客户端的channel处理器
    .childHandler(new ChannelInitializer<NioSocketChannel>() {
        @Override
        //NioSocketChannel 与客户端的channel
        protected void initChannel(NioSocketChannel ch) throws Exception {
            //4、解码消息，将字节转换为字符串
            ch.pipeline().addLast(new StringDecoder());
            //5、入站消息处理器
            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                /**
                 * 入站消息的处理
                 * @param ctx channel的上下文
                 * @param msg 入站消息
                 */
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    NioSocketChannel channel = (NioSocketChannel)ctx.channel();
                    InetSocketAddress inetSocketAddress = channel.remoteAddress();
                    //6、打印消息
                    log.info("收到来自[{}]的消息：[{}]",inetSocketAddress.toString(),msg);
                }
            });
        }
    })
    //7、绑定端口
    .bind(nettyHelloProt);
```



### 2.3 客户端

```java
//客户端启动器
new Bootstrap()
        //1、配置事件轮询组，NioEventLoopGroup: 即NIO的selector模式
        .group(new NioEventLoopGroup())
        //2、配置客户端的通道
        .channel(NioSocketChannel.class)
        //3、配置客户端的处理器
        .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            //初始化客户端
            protected void initChannel(NioSocketChannel ch) throws Exception {
                //4、编码消息，将字符串转换成字节数组
                ch.pipeline().addLast(new StringEncoder());
            }
        })
        //5、连接客户端
        .connect(new InetSocketAddress(nettyHelloProt))
        //6、阻塞线程，直到与服务器建立连接
        .sync()
        //7、获取客户端的通道，即NioSocketChannel
        .channel()
        //8、发送消息
        .writeAndFlush("hello netty");
```

### 2.4 流程梳理

```mermaid
sequenceDiagram
participant s as 服务端
participant c as 客户端

s ->> s:创建启动器 new ServerBootstrap()
s ->> s:配置事件轮询组，NioEventLoopGroup: selector的多线程模式
s ->> s:配置服务端通道 channel()
s ->> s:配置与客户端channel的处理器 childHandler()
s ->> s:配置端口 bind()

c ->> c:创建启动器 new Bootstrap()
c ->> c:配置通道 channel()
c ->> c:配置处理器 handler()

c ->> s:连接服务端 connect()
c ->> c:阻塞线程等待成功连接服务端 sync()
s ->> s:ChannelInitializer.initChannel(NioSocketChannel ch) 
s ->> c:连接成功
c ->> c:结束阻塞
c ->> c:ChannelInitializer.initChannel(NioSocketChannel ch)
c ->> c:发送"hello netty"消息
c ->> c:将消息编码 StringEncoder.encode()
c ->> s:发送编码后的消息
s ->> s:解码消息 StringDecoder.decode()
s ->> s:打印解码后的消息 ChannelInboundHandlerAdapter.channelRead()

```

#### 💡 提示

树立正确的观念

**channel 、msg、pipeline 、handler、eventLoop的关系**

* 把 channel 理解为**数据的通道**
* 把 msg 理解为**通道中流动的数据**，最开始输入是 ByteBuf，但经过 pipeline **多道工序的加工**，会变成其它类型对象，最后输出又变成 ByteBuf
* 把 handler 理解为**数据的处理工序**
  * **工序有多道，合在一起就是 pipeline**，pipeline 负责发布事件（读、读取完成...）传播给每个 handler， handler 对自己感兴趣的事件进行处理（重写了相应事件处理方法）
  * handler 分 Inbound（输入） 和 Outbound（输出） 两类

* 把 eventLoop 理解为**处理数据的工人**
  * 工人可以管理多个 channel 的 io 操作，并且一旦工人负责了某个 channel，就要负责到底（绑定）
  * 工人既可以执行 io 操作，也可以进行任务处理，每位工人有任务队列，队列里可以堆放多个 channel 的待处理任务，任务分为普通任务、定时任务
  * **工人按照 pipeline 顺序，依次按照 handler 的规划（代码）处理数据**，可以为每道工序指定不同的工人

## 3. 组件

### 3.1 EventLoop

#### 3.1.1 事件循环对象

EventLoop本质上是一个任务执行器，同时维护了一个Selector，并且包含一个run方法来处理channel中源源不断的io事件

**继承关系**

![](./images/eventLoop-extend.png)

- 继承了`java.util.concurrent.ScheduledExecutorService`任务执行器，因此包含了所有的线程池方法
- 继承了`io.netty.util.concurrent.EventExecutor`，这个接口包含了：
  - `boolean inEventLoop(Thread thread);`：**判断一个线程是否属于该EventLoop**

#### 3.1.2 事件循环组对象

`EventLoopGroup`是一组`EventLoop`，通过`EventLoopGroup`的`register`方法将`channel`绑定到某一个

`EventLoop`，这个`channel`后续所有的io事件都由此`EventLoop`处理（保证了io事件处理时的线程安全）

**`EventLoopGroup`的实现类**

- NioEventLoopGroup：可处理io事件、普通任务和定时任务
- DefaultEventLoopGroup：可普通任务和定时任务

**简单的示例**

```java
EventLoopGroup group = new DefaultEventLoopGroup(2);
//第一个eventLoop
EventLoop eventLoop1 = group.next();
//第二个eventLoop
EventLoop eventLoop2 = group.next();
//第一个eventLoop
EventLoop eventLoop3 = group.next();
//第二个eventLoop
EventLoop eventLoop4 = group.next();

//由于只有两个线程所以只有两个eventLoop，那么第一次和第三次获取的是同一个eventLoop，同理第二次和第四次获取的是同一个eventLoop
Assert.assertEquals(eventLoop1,eventLoop3);
Assert.assertEquals(eventLoop2,eventLoop4);
```

**处理普通任务和定时任务**

```java
EventLoopGroup group = new DefaultEventLoopGroup(2);

//添加任务
group.next().execute(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    log.debug("给eventLoop添加任务");
});

//添加定时任务
group.next().scheduleAtFixedRate(
        () -> log.debug(String.valueOf(LocalDateTime.now().getSecond()))
        //多少秒后执行第一次
        ,0
        //每隔多久执行一次
        ,1
        //参数3的时间单位
        , TimeUnit.SECONDS
);

log.debug("主线程");

try {
    Thread.sleep(1000*5);
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

控制台打印：

```tex
22:17:02.988 [main] DEBUG top.ersut.netty.EventLoopTest - 主线程
22:17:03.006 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 3
22:17:03.989 [defaultEventLoopGroup-2-1] DEBUG top.ersut.netty.EventLoopTest - 给eventLoop添加任务
22:17:03.989 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 3
22:17:04.988 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 4
22:17:05.988 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 5
22:17:06.989 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 6
22:17:07.989 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 7
```

[示例代码#eventLoopGroupTest](./netty_demo/src/main/test/top/ersut/netty/EventLoopTest.java)

##### 💡 优雅关闭

优雅关闭 `shutdownGracefully` 方法。该方法会首先切换 `EventLoopGroup` 到关闭状态从而拒绝新的任务的加入，然后在任务队列的任务都处理完成后，停止线程的运行。从而确保整体应用是在正常有序的状态下退出的

##### 多个EventLoopGroup，处理不同的事件

[客户端代码](./netty_demo/src/main/test/top/ersut/netty/EventLoopTest.java)：

```java
//两个客户端各发送两条消息
public void client() throws InterruptedException {
    NioEventLoopGroup group = new NioEventLoopGroup();
    Channel channel = new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringEncoder());
                }
            })
            .connect(new InetSocketAddress(PORT))
            .sync()
            .channel();
    Channel channel2 = new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringEncoder());
                }
            })
            .connect(new InetSocketAddress(PORT))
            .sync()
            .channel();
    channel.writeAndFlush("123");
    Thread.sleep(1000);
    channel2.writeAndFlush("abc");
    Thread.sleep(1000);
    channel.writeAndFlush("456");
    Thread.sleep(1000);
    channel2.writeAndFlush("def");

    group.shutdownGracefully().sync();
    channel.close();
    channel2.close();
}
```

[服务端代码](./netty_demo/src/main/test/top/ersut/netty/EventLoopTest.java)：

```java
public static void groupServer(){
    new ServerBootstrap()
            /**
             * 参数1 parentGroup：处理 NioServerSocketChannel 的accept
             * 参数2 childGroup：处理 NioSocketChannel 的 读写
             */
            .group(new NioEventLoopGroup(1),new NioEventLoopGroup(2))
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf message = (ByteBuf) msg;
                            log.debug(message.toString(StandardCharsets.UTF_8));
                        }
                    });
                }
            })
            .bind(PORT);
}
```

打印信息：

```tex
22:36:20.907 [nioEventLoopGroup-3-1] DEBUG top.ersut.netty.EventLoopTest - 123
22:36:21.891 [nioEventLoopGroup-3-2] DEBUG top.ersut.netty.EventLoopTest - abc
22:36:22.890 [nioEventLoopGroup-3-1] DEBUG top.ersut.netty.EventLoopTest - 456
22:36:23.889 [nioEventLoopGroup-3-2] DEBUG top.ersut.netty.EventLoopTest - def
```

##### 💡进程名解释

nioEventLoopGroup-3-2

- 其中的”3“代表第3个进程池
- 其中的“2”代表EventLoopGroup中的第二个EventLoop

**通过打印的信息可以看出同一个channel中的信息在固定的EventLoop中处理，即channel与EventLoop绑定**

图解:

![](images/0042.png)

##### 给通道处理器指定EventLoopGroup

客户端代码与上边的一致

[服务端代码](./netty_demo/src/main/test/top/ersut/netty/EventLoopTest.java)：

```java
public static void pipelineAppointGroupServer(){
    EventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup(2);
    new ServerBootstrap()
            /**
             * 参数1 parentGroup：处理 NioServerSocketChannel 的accept
             * 参数2 childGroup：处理 NioSocketChannel 的 读写
             */
            .group(new NioEventLoopGroup(),new NioEventLoopGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf message = (ByteBuf) msg;
                            log.debug(message.toString(StandardCharsets.UTF_8));
                            //传给pipeline中的下一个处理器
                            super.channelRead(ctx,msg);
                        }
                    });
                    ch.pipeline().addLast(defaultEventLoopGroup,"default",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf message = (ByteBuf) msg;
                            log.debug(message.toString(StandardCharsets.UTF_8)+"-default");
                        }
                    });
                }
            })
            .bind(PORT);
}
```

- **当多个通道处理器时，需要通过`super.channelRead(ctx,msg)`将消息传递给下一个处理器**

- **通过`addLast(EventExecutorGroup group, String name, ChannelHandler handler)`方法给通道处理器指定EventLoopGroup**
  - 参数1：指定EventLoopGroup
  - 参数2：给通道处理器指定名称
  - 参数3：通道处理器

控制台打印：

```tex
22:54:55.282 [nioEventLoopGroup-4-1] DEBUG top.ersut.netty.EventLoopTest - 123
22:54:55.283 [defaultEventLoopGroup-2-1] DEBUG top.ersut.netty.EventLoopTest - 123-default
22:54:56.246 [nioEventLoopGroup-4-2] DEBUG top.ersut.netty.EventLoopTest - abc
22:54:56.247 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - abc-default
22:54:57.247 [nioEventLoopGroup-4-1] DEBUG top.ersut.netty.EventLoopTest - 456
22:54:57.247 [defaultEventLoopGroup-2-1] DEBUG top.ersut.netty.EventLoopTest - 456-default
22:54:58.247 [nioEventLoopGroup-4-2] DEBUG top.ersut.netty.EventLoopTest - def
22:54:58.247 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - def-default
```

**通过打印的信息可以看出名为 default 的通道处理器使用了其他线程**

图解：

![](./images/0041.png)

##### 💡多个通道处理器之间是怎么切换EventLoop的

追踪`super.channelRead(ctx,msg);`方法可以找到如下源码

```java
static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
    final Object m = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);
    EventExecutor executor = next.executor();
    //判断下一个EventLoop与当前EventLoop是否为同一个线程
    if (executor.inEventLoop()) {
        //是同一个，直接运行
        next.invokeChannelRead(m);
    } else {
        //不是同一个，将需要执行的代码作为任务添加到给下一个EventLoop(切换EventLoop)
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelRead(m);
            }
        });
    }
}
```

* 如果两个 handler 绑定的是同一个线程，那么就直接调用
* 否则，把要调用的代码封装为一个任务对象，由下一个 handler 的线程来调用

## 3.2 Channel

注意这里的Channel是netty定义的，不是Java自己的Channel

Channel的主要方法：

- eventLoop()：Channel所在的eventLoop
- close()：用来关闭Channel
- closeFuture()：Channel关闭成功后的回调
  - sync()：**同步阻塞**，等待channel关闭
  - addListener()：**异步非阻塞**，等待 channel 关闭
- writeAndFlush()：写入内容并发送
- wirte():写入数据

**封装客户端启动器**

```java
private static final int PORT = 15218;

public static Bootstrap bootstrap(){
    return bootstrap(new NioEventLoopGroup());
}
public static Bootstrap bootstrap(NioEventLoopGroup nioEventLoopGroup){
    return new Bootstrap()
            .group(nioEventLoopGroup)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    //打印详细日志
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new StringEncoder());
                }
            });
}
```

**LoggingHandler是netty的日志处理器**，可以查看channel的运行情况等

[示例代码#bootstrap](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

### 3.2.1 Channel的连接

```java
ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));

Channel channel = channelFuture.channel();
channel.writeAndFlush("not sync");
log.info("channel isActive:{}",channel.isActive());
```

[示例代码PrematureGetChannelTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

控制台打印：

```tex
2023-06-17 22:09:09 [main] INFO  top.ersut.netty.ChannelTest - channel isActive:false
2023-06-17 22:09:09 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x35a9f64c] REGISTERED
2023-06-17 22:09:09 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x35a9f64c] CONNECT: 0.0.0.0/0.0.0.0:15218
2023-06-17 22:09:09 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x35a9f64c, L:/192.168.123.137:52198 - R:0.0.0.0/0.0.0.0:15218] ACTIVE
```

`id: 0x35a9f64c, L:/192.168.123.137:52198 - R:0.0.0.0/0.0.0.0:15218] ACTIVE`这行日志代表channel成功连接

#### 上述代码存在的问题

- **过早的获取channel，导致消息发送失败**

#### 问题分析

通过上述日志中 `channel isActive:false`可以看到 channel 是非活动状态，发送消息的代码是在此条日志之前

非活动状态的channel代表客户端与服务端还未成功连接，所以这条消息发送失败。

#### 解决方案：ChannelFuture

JDK的Future是获取异步线程的结果，他与ChannelFuture的关系：

![](./images/ChannelFuture.png)

ChannelFuture继承自JDK的Future，并且进行了增强。

##### ChannelFuture中常用的方法

- channel()：获取channel
- sync()：同步等待结果
- addListener()：添加监听器

##### ChannelFuture的获取

```
ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));
```

通过connect方法获取的ChannelFuture

**connect方法是异步的，那么不等待连接成功就返回了，因此通过ChannelFuture.channel()获取channel并不一定是已连接的。**

##### 方案1：通过sync()同步等待结果

```java
ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));

//新增代码，等待异步线程的结果
channelFuture.sync();
Channel channel = channelFuture.channel();
channel.writeAndFlush("sync");
log.info("channel isActive:{}",channel.isActive());
```

[示例代码ChannelBySyncTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

控制台打印：

```java
2023-06-17 23:31:49.861 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x5f7acf4e] REGISTERED
2023-06-17 23:31:49.865 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x5f7acf4e] CONNECT: 0.0.0.0/0.0.0.0:15218
2023-06-17 23:31:49.871 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x5f7acf4e, L:/192.168.123.137:54507 - R:0.0.0.0/0.0.0.0:15218] ACTIVE
2023-06-17 23:31:49.877 [main] INFO  top.ersut.netty.ChannelTest - channel isActive:true
2023-06-17 23:31:49.895 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x5f7acf4e, L:/192.168.123.137:54507 - R:0.0.0.0/0.0.0.0:15218] WRITE: 4B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 73 79 6e 63                                     |sync            |
+--------+-------------------------------------------------+----------------+
2023-06-17 23:31:49.896 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x5f7acf4e, L:/192.168.123.137:54507 - R:0.0.0.0/0.0.0.0:15218] FLUSH
```

服务器收到了消息：

```tex
2023-06-17 23:32:42.395 [nioEventLoopGroup-3-1] INFO  top.ersut.netty.ChannelTest - sync
```

##### 方案2：通过addListener()添加监听器（回调方式）

```java
ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));

//等待连接成功
channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
    Channel channel = channelFuture1.channel();
    channel.writeAndFlush("ChannelFutureListener");
    log.info("channel isActive:{}",channel.isActive());
});
```

[示例代码ChannelByListenerTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

控制台打印：

```tex
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0xd294f3e6] REGISTERED
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0xd294f3e6] CONNECT: 0.0.0.0/0.0.0.0:15218
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0xd294f3e6, L:/192.168.123.137:54912 - R:0.0.0.0/0.0.0.0:15218] WRITE: 21B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 43 68 61 6e 6e 65 6c 46 75 74 75 72 65 4c 69 73 |ChannelFutureLis|
|00000010| 74 65 6e 65 72                                  |tener           |
+--------+-------------------------------------------------+----------------+
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0xd294f3e6, L:/192.168.123.137:54912 - R:0.0.0.0/0.0.0.0:15218] FLUSH
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.ChannelTest - channel isActive:true
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0xd294f3e6, L:/192.168.123.137:54912 - R:0.0.0.0/0.0.0.0:15218] ACTIVE
```

服务器收到了消息：

```tex
2023-06-17 23:48:29.858 [nioEventLoopGroup-3-2] INFO  top.ersut.netty.ChannelTest - ChannelFutureListener
```

##### 💡两种方案线程为什么不一样

比较两个方案中，`log.info("channel isActive:{}",channel.isActive());`都是打印的 channel isActive:true ，但是他们的线程不一样：

- sync()方式是main线程（主线程），该方式是同步等待结果，所以在主线程打印。
- addListener()方式是 nioEventLoopGroup-2-1 线程，该方式是在子线程中连接成功后直接运行我们的代码，所以在 nioEventLoopGroup-2-1 线程

### 3.2.2 Channel的关闭

```java
NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
ChannelFuture channelFuture = bootstrap(eventLoopGroup).connect(new InetSocketAddress(PORT));
 
//等待连接成功
channelFuture.sync();
Channel channel = channelFuture.channel();
channel.writeAndFlush("channelClose");
log.info("close......");
channel.close();
//期望channel成功关闭后再运行
log.info("closed");
```

[示例代码ChannelCloseTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

控制台打印：

```tex
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889] REGISTERED
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889] CONNECT: 0.0.0.0/0.0.0.0:15218
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 - R:0.0.0.0/0.0.0.0:15218] ACTIVE
2023-06-18 13:03:48 [main] INFO  top.ersut.netty.ChannelTest - close......
2023-06-18 13:03:48 [main] INFO  top.ersut.netty.ChannelTest - closed
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 - R:0.0.0.0/0.0.0.0:15218] WRITE: 12B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 68 61 6e 6e 65 6c 43 6c 6f 73 65             |channelClose    |
+--------+-------------------------------------------------+----------------+
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 - R:0.0.0.0/0.0.0.0:15218] FLUSH
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 - R:0.0.0.0/0.0.0.0:15218] CLOSE
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 ! R:0.0.0.0/0.0.0.0:15218] INACTIVE
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 ! R:0.0.0.0/0.0.0.0:15218] UNREGISTERED
```

`[id: 0x3908f889, L:/192.168.123.137:53074 - R:0.0.0.0/0.0.0.0:15218] CLOSE`这行日志代表channel成功关闭

#### 上述代码存在的问题

1. 由于客户端关闭是在NioEventLoop线程中进行的，所以主线程中不能正确的执行关闭后的任务
2. 关闭Channel后进程没有终止

#### 问题1分析

通过日志我们可以发现`log.info("closed");`执行在channel成功关闭之前，与我们的期望（channel成功关闭后再运行）不一致

#### 问题1的解决方案：closeFuture()

通过channel的closeFuture()可以获取关闭channel的ChannelFuture

解决方案

- 通过sync()同步等待结果，[示例代码ChannelCloseFutureSyncTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)
- 通过addListener()添加监听器[示例代码ChannelCloseFutureListenerTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

此处不再过多叙述与connect（连接服务端）返回的ChannelFuture是一个类

- connect()的ChannelFuture是关于channel连接成功的
- closeFuture()的ChannelFuture是关于channel关闭成功的

#### 问题2分析

除主线程外，每个 **EventLoop 都有自己的线程**，那么当主线程代码执行完成后，由于  **EventLoop 的线程还未结束，所以进程不会结束。**

**只有结束所有的线程，进程才会终止。**

#### 问题2的解决方案

之前提到过NioEventLoopGroup提供了shutdownGracefully方法用来**停止其包含的所有EventLoop对应的线程**。

注意：shutdownGracefully方法**同时会关闭channel**

```java
NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
ChannelFuture channelFuture = bootstrap(eventLoopGroup).connect(new InetSocketAddress(PORT));

//等待连接成功
channelFuture.sync();
Channel channel = channelFuture.channel();
channel.writeAndFlush("channelCloseFutureListener");
log.info("channel isActive:{}",channel.isActive());

ChannelFuture closeFuture = channel.closeFuture();
closeFuture.addListener((ChannelFutureListener) future -> {
    Channel channel1 = future.channel();
    log.info("channel isActive:{}",channel1.isActive());
    log.info("closed");
});

log.info("close......");
//eventLoopGroup中还有线程，关闭eventLoopGroup中的线程，同时会关闭channel
eventLoopGroup.shutdownGracefully();
```

[示例代码ChannelCloseFutureListenerTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

控制台打印：

```tex
2023-06-18 16:41:53 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3] REGISTERED
2023-06-18 16:41:53 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3] CONNECT: 0.0.0.0/0.0.0.0:15218
2023-06-18 16:41:53 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3, L:/192.168.123.137:59394 - R:0.0.0.0/0.0.0.0:15218] ACTIVE
2023-06-18 16:41:53 [main] INFO  top.ersut.netty.ChannelTest - close......
2023-06-18 16:41:53 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3, L:/192.168.123.137:59394 - R:0.0.0.0/0.0.0.0:15218] WRITE: 22B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 68 61 6e 6e 65 6c 43 6c 6f 73 65 46 75 74 75 |channelCloseFutu|
|00000010| 72 65 53 79 6e 63                               |reSync          |
+--------+-------------------------------------------------+----------------+
2023-06-18 16:41:53 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3, L:/192.168.123.137:59394 - R:0.0.0.0/0.0.0.0:15218] FLUSH
2023-06-18 16:41:56 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3, L:/192.168.123.137:59394 ! R:0.0.0.0/0.0.0.0:15218] INACTIVE
2023-06-18 16:41:56 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3, L:/192.168.123.137:59394 ! R:0.0.0.0/0.0.0.0:15218] UNREGISTERED
2023-06-18 16:41:58 [main] INFO  top.ersut.netty.ChannelTest - closed
```

成功解决！

