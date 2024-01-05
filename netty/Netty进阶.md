# Netty进阶



## 一、粘包与半包

需求：客户端发送5条"0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09"到服务端，服务端可正确解析每一条。

### 1.1 粘包现象&半包现象

[相关代码](./netty_demo/src/main/test/top/ersut/netty/stickhalfpackage/StickHalfPackageTest.java)

**客户端发送数据部分代码：**

```java

...

log.info("发送数据");
for (int i = 0; i < 5; i++) {
    ByteBuf byteBuf = ctx.alloc().buffer();
    //每条信息10个字节
    byteBuf.writeBytes(new byte[]{0,1, 2, 3, 4, 5, 6, 7,8,9});
    ctx.writeAndFlush(byteBuf);
}
ctx.channel().close();

...

```

客户端一次性发出来5条数据出去。

**服务端部分代码：**

```java

...

ServerBootstrap serverBootstrap = new ServerBootstrap();
serverBootstrap.channel(NioServerSocketChannel.class)
//NioSocketChannel 中ByteBuf 大小限制 16个字节
.childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16))
.group(boos, works)
.childHandler(new ChannelInitializer<NioSocketChannel>() {
    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = ((ByteBuf) msg);
                log.info("byteBuf.writerIndex():[{}]",byteBuf.writerIndex());
                log.info("byteBuf.capacity():[{}]",byteBuf.capacity());
                super.channelRead(ctx, msg);
            }
        });
        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
    }
});

...

```

其中 `.childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16))`是控制服务端接收数据的ByteBuf大小，目前设置为16字节。

**服务端控制台打印：**

![](./images/StickHalfPackageLog.png)

#### 1.1.1  现象的原因

##### **应用层**

- **粘包**：接收方设置的ByteBuf 太大（netty默认为1024），即`ChannelOption.RCVBUF_ALLOCATOR`

- **半包**：当接收的数据比设置的ByteBuf 大



##### 滑动窗口

什么是滑动窗口？

- TCP 每次传输的数据，称为segment（被叫做TCP段），**每发送一个段都需要等待应答（ack）**，这样有个很大的弊端，如果 **TCP段响应的时间越长性能越差**，例如发送5个段，第三个段的ack响应很慢，那这个期间第四五个段还在等待发送，这样大大降低了性能。

  图示：

  ![](images/0049.png)

- 为了解决这个问题，出现了滑动窗口。**滑动窗口中，可以同时发送多条消息（无需等待ack的返回），这样性能大大提升。**

  - **当消息塞满滑动窗口后，其他消息等待发送，**滑动窗口中有消息接收到ack后，窗口继续滑动发送后续的消息。
  - **接收方也会维护一个窗口，只有落在窗口中的数据才会接收。**

  图示：

  ![](images/0051.png)

- **粘包**：假设一条消息大小为128kb，接收方的滑动窗口还剩余512kb，如果接收方处理的不及时，下一条消息也发送过来了，就会造成粘包。

- **半包**：假设一条消息大小为128kb，接收方窗口只剩余64kb，这时接收方不能完整接收这条消息，只能先接收消息前半部分的64kb，这样就造成了半包。

##### MSS限制

- **链路层对一次能发送的数据大小称为MTU**（Maximum Transmission Unit），不同的链路设备（网卡、交换机、等）MTU值也不同

  - 以太网的MTU值为1500
  - FDDI（光纤分布式数据接口）的 MTU 是 4352
  - 回环地址（不走网卡）的MTU一般为65536，我mac电脑为16384

- MSS是最大TCP段的长度，他是**MTU刨去TCP头和ip头后剩余的大小**。

  - ipv4 tcp 头占用 20 bytes，ip 头占用 20 bytes，因此以太网 MSS 的值为 1500 - 40 = 1460

  - 图示

    ![](images/MTU&MSS.png)

- TCP在传输大量数据时会按照MSS的大小对**数据分片发送**，这里会造成半包

  - 图示（假设MSS是80kb）

    ![](images/MSS-section.png)

- 在TCP三次握手时会协商MSS值的大小，在**两者之间选择一个最小值作为MSS**（实际情况下中间还有会路由、交换机设备，会在整个网络设备中选择最小的MSS）

##### Nagle 算法

* 即使发送一个字节，也需要加入 tcp 头和 ip 头，也就是总字节数会使用 41 bytes，非常不经济。因此**为了提高网络利用率，tcp 希望尽可能发送足够大的数据，这就是 Nagle 算法产生的缘由**
* 该算法是指发送端即使还有应该发送的数据，但如果这部分数据很少的话，则进行延迟发送
  * 如果 SO_SNDBUF 的数据达到 MSS，则需要发送
  * 如果 SO_SNDBUF 中含有 FIN（表示需要连接关闭）这时将剩余数据发送，再关闭
  * 如果 TCP_NODELAY = true，则需要发送
  * 已发送的数据都收到 ack 时，则需要发送
  * 上述条件不满足，但发生超时（一般为 200ms）则需要发送
  * 除上述情况，延迟发送，这就造成了粘包



##### **粘包**总结

- 应用层：接收方设置的ByteBuf 太大（netty默认为1024），即`ChannelOption.RCVBUF_ALLOCATOR`
- 滑动窗口：假设一条消息大小为128kb，接收方的滑动窗口还剩余512kb，如果接收方处理的不及时，下一条消息也发送过来了，就会造成粘包。
- Nagle 算法：延迟发送会造成了粘包



##### **半包**总结

- 应用层：当接收的数据比设置的ByteBuf 大
- 滑动窗口：假设一条消息大小为128kb，接收方窗口只剩余64kb，这时接收方不能完整接收这条消息，只能先接收消息前半部分的64kb，这样就造成了半包。
- MSS限制：TCP在传输大量数据时会按照MSS的大小对**数据分片发送**



### 1.2 解决方案 

#### 1.2.1 行解码器

 [相关代码](netty_demo/src/main/test/top/ersut/netty/stickhalfpackage/SolutionByLineTest.java)

**根据定界符：换行(\n or \r\n) ，识别一条完整的消息**



**服务端通过 `LineBasedFrameDecoder`解码消息**

- 构造方法：`public LineBasedFrameDecoder(final int maxLength)`
  - maxLength：限制一条消息最大的长度，如果超出抛出`TooLongFrameException `异常

**客户端通过`LineEncoder`编码消息；**



服务端部分代码：

```java
NioEventLoopGroup boss = new NioEventLoopGroup();
NioEventLoopGroup works = new NioEventLoopGroup();
try {
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(boss, works);
    serverBootstrap.channel(NioServerSocketChannel.class);
    serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            //根据 换行符 进行解码
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
    });
    ChannelFuture channelFuture = serverBootstrap.bind(PROT);

    channelFuture.addListener(future -> {
        if (future.isSuccess()) {
            log.info("启动成功");
        } else {
            log.error("启动失败", future.cause());
        }
    });
    channelFuture.channel().closeFuture().sync();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
} finally {
    boss.shutdownGracefully();
    works.shutdownGracefully();
}
```



客户端代码：

```java
NioEventLoopGroup work = new NioEventLoopGroup();
try {
    ChannelFuture channelFuture = new Bootstrap()
            .group(work)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    //在每条消息最后添加 换行符 的编码器
                    ch.pipeline().addLast(new LineEncoder());
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.info("发送数据");
                            for (int i = 0; i < 5; i++) {
                                //这里要写入 CharSequence类型， 因为LineEncoder只处理 CharSequence类型 的数据
                                ctx.writeAndFlush(new String(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}));
                            }
                            ctx.channel().close();
                        }
                    });
                }
            })
            .connect(new InetSocketAddress(PROT));
    Channel channel = channelFuture.channel();

    channelFuture.addListener(future -> {
        if (future.isSuccess()) {
            log.info("启动成功");
        } else {
            log.error("启动失败", future.cause());
        }
    });
    channel.closeFuture().sync();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
} finally {
    work.shutdownGracefully();
}
```



客户端发出后，服务端的打印:

![](./images/LineDecode-log.png)



#### 1.2.2 LTC解码器

[相关代码](netty_demo/src/main/test/top/ersut/netty/stickhalfpackage/SolutionByLengthFieldTest.java) 

**根据长度字段的解析方法**



获取到**长度字段的值**后，**按照长度值的大小，向后读取字节个数**，这些字节就是消息的内容。

例如，接收到的消息为：**0x05**,0x01,0x02,0x03,0x04,0x05,**0x03**,0x06,0x07,0x08

**0x05**为长度字段，那向后继续读取5个字节，则是消息的内容，即0x01,0x02,0x03,0x04,0x05

该消息完整解析如下：

| 长度 | 消息的内容               |
| ---- | ------------------------ |
| 0x05 | 0x01,0x02,0x03,0x04,0x05 |
| 0x03 | 0x06,0x07,0x08           |

这个示例是**长度字段表示消息内容的长度**，另一种方式是**长度字段表示整体消息（包含长度字段）的长度**，就不写了



**netty提供了`LengthFieldBasedFrameDecoder`类实现了长度字段解析消息**

`LengthFieldBasedFrameDecoder`对消息分成了两部分

- **头部分：包含了长度字段，另外允许添加一些其他字段**
- 内容部分：真实的消息

`LengthFieldBasedFrameDecoder`的构造方法：

```java
public LengthFieldBasedFrameDecoder(
        int maxFrameLength,
        int lengthFieldOffset, int lengthFieldLength,
        int lengthAdjustment, int initialBytesToStrip)
```

- maxFrameLength：限制消息的最大长度
- lengthFieldOffset：长度字段在消息中的偏移量，长度字段前有附加的头信息造成的。
- lengthFieldLength：长度字段占用的字节个数
- lengthAdjustment：长度字段后的头信息还有多少个字节，
- initialBytesToStrip：从整个消息中剥离出去多少个字节（从头部开始剥离），0则不剥离



**netty提供了`LengthFieldPrepender`类实现了长度字段编码消息**

构造方法

```
public LengthFieldPrepender(int lengthFieldLength, int lengthAdjustment)
```

- lengthFieldLength：长度字段占用的字节数
- lengthAdjustment：附加的头信息占用的字节（需要是负数）

**服务端示例代码**：

```java
NioEventLoopGroup boss = new NioEventLoopGroup();
NioEventLoopGroup works = new NioEventLoopGroup();
try {
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(boss, works);
    serverBootstrap.channel(NioServerSocketChannel.class);
    serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            //长度字段的值是消息内容的长度
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 3, 2)); //1处
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
    });
    ChannelFuture channelFuture = serverBootstrap.bind(PROT);

    channelFuture.addListener(future -> {
        if (future.isSuccess()) {
            log.info("启动成功");
        } else {
            log.error("启动失败", future.cause());
        }
    });
    channelFuture.channel().closeFuture().sync();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
} finally {
    boss.shutdownGracefully();
    works.shutdownGracefully();
}
```

1处 是 长度字段编码器，指出**消息最大为1024**，**长度字段是第一个和第二个字节**，并且长度字段后有**三个字节为附加头**，**剥离掉两个字节（即长度字段）**。



**客户端示例代码**：

```java
NioEventLoopGroup work = new NioEventLoopGroup();
try {
    ChannelFuture channelFuture = new Bootstrap()
            .group(work)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    //长度字段 编码器
                    ch.pipeline().addLast(new LengthFieldPrepender(2, -3));//2处
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.info("发送数据");
                            for (int i = 0; i < 5; i++) {
                                ByteBuf buffer = ctx.alloc().buffer();
                                //自定义的头信息
                                byte[] header = new byte[]{(byte) 0xff, (byte) 0xaa};
                                buffer.writeBytes(header);
                                //数据
                                byte[] data = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
                                buffer.writeBytes(data);

                                ctx.writeAndFlush(buffer);
                            }
                            ctx.channel().close();
                        }
                    });
                }
            })
            .connect(new InetSocketAddress(PROT));
    Channel channel = channelFuture.channel();

    channelFuture.addListener(future -> {
        if (future.isSuccess()) {
            log.info("启动成功");
        } else {
            log.error("启动失败", future.cause());
        }
    });
    channel.closeFuture().sync();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
} finally {
    work.shutdownGracefully();
}
```

2处是长度字段编码器，指出长度字段占用了2个字节，并且还有3个字节为附加的头信息



**服务端控制台的打印**：

![](./images/LengthFieldDecode-log.png)
