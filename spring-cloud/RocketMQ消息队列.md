# RocketMQ消息队列



## 三大核心组件

### NameServer：注册中心

- Broker的注册中心
- 管理Broker的信息，并提供心跳机制金策是否存活。
- 客户端通过NameServer查找**Broker**
  - **5.0版本后客户端不直接访问NameServer而是通过Proxy转发数据**


### Broker：消息存储

- 接收生产者消息，并持久化消息
- 推送消息给消费者
- 根据主题（TOPIC）和队列（QUEUE）组织数据
- 支持集群
  - **多主模式**：多个主节点进行集群
    - 优点：部署简单
    - 缺点：宕机的节点在恢复前不可订阅，消息实时性会受到影响。
  - **多主从复制（异步复制）模式**：每个主节点带从节点
    - 优点：主节点宕机后，消费者仍然可以从Slave消费，而且此过程对应用透明，不需要人工干预，性能同多Master模式几乎一样。主节点宕机后**从节点替换为主节点**（4.5后支持自动切换）
    - 缺点：Master宕机，磁盘损坏情况下会丢失少量消息。

### Proxy：消息代理层

>  5.0版本后支持

把Broker职责拆分到Proxy，Broker专注于存储。



Proxy支持

- 提供负载均衡
- 访问控制
- 协议适配（MQTT等）
- 等....



## 领域模型

### 概述

**RocketMQ使用的发布订阅模型**

模型图:

![](./images/rocket-mq-model.png)

**Topic**：主题，消息容器的名称，全局唯一。

**Message Queue**：消息队列，真实存储消息的地方。

- 主题内有多个消息队列组成



**Message**：消息，RocketMQ最小传输单元。

- 存储在消息队列中。



**Producer**：消息的生产者，创建消息并发送到RocketMQ中。

**Consumer**：消息的消费者，处理收到的消息。

**Subscription**：订阅关系，处理消息的过滤规则。

**Consumer Group**：消费者分组，把相同的消费者放入一个组中，统一定义订阅关系（Subscription）、重试机制、消费者负责均衡等。

- 一个消费者组可以有多个订阅关系。



#### 一条消息的生产到消费

```mermaid
graph LR
st([开始])
en([结束])
msg-pro[生产者]
Topic[匹配Topic]
queue[存入消息队列]
msg-con-group[消费者组]
msg-con[消费者1]
msg-con2[消费者2]
msg-con3[消费者3]
sub[通过订阅关系过滤消息]
st-->msg-pro
msg-pro--消息-->Topic
Topic--消息-->queue
queue--消息-->sub
sub--消息-->msg-con-group
msg-con-group--消息-->msg-con
msg-con---->en
msg-con-group-.->msg-con3
msg-con-group-.->msg-con2

```







### Topic：主题



### Queue：队列



### Message：消息



### Producer：生产者



### Consumer：消费者