# Seata分布式事务



## 全局锁

**每个全局事务都有一个全局锁**

### 全局锁的内容

- 全局事务id
- 分支事务id
- **锁定资源的信息**

  - 表名

  - 行
- 等

chatgpt的回答：

<img src="./images/seata-lock.png" style="zoom: 50%;" />

### 全局锁如何实现写隔离

官方的写隔离文档：https://seata.apache.org/zh-cn/docs/overview/what-is-seata#%E6%95%B4%E4%BD%93%E6%9C%BA%E5%88%B6

官方文档指出了描述了写隔离的执行逻辑以及写隔离通过全局锁实现，但是<span style="color:red">未说明全局锁是如何实现的写隔离</span>

**一些个人理解**：

去TC查询所有的全局锁是否包含将要锁定的资源

- 如果没有则**获取该全局事务的全局锁，并往全局锁种写入该资源进行锁定**
- 如果有，则重新查询该资源