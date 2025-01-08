# 事务

## 什么是事务
事务是数据库的最小工作单元，其作用是对数据库中的一组操作要么都执行，要么都不执行

例如：

1. 5条sql，第三条报错那么整体都不执行;前两条sql虽然没有报错但是也不执行，后两条因为第三条报错不再运行。
2. 业务中如果程序异常，那这条业务中的sql都可以不执行。

## 什么场景下应用
- 转账业务:张三转给李四500元,分两步

	1. 张三账户减少500
	2. 李四账户增加500

  我们不能因为李四账户增加500失败而张三账户减少500，所已两个步骤要一起撤回（这个撤回操作事务中成为回滚）


## <div id="characteristic"></div>事务的四个特性（ACID）

### 原子性

原子性是指事务包含的所有操作要么全部成功，要么全部失败回滚。

### 一致性

一致性是指事务必须使数据库从一个一致性状态变换到另一个一致性状态，也就是说一个事务执行之前和执行之后都必须处于一致性状态

例如：

张三银行存款1000元，李四银行存款500元，他们俩个总共1500元。

张三转给李四200元，张三剩余800元，李四增加到700元；他们两个加起来还是1500元。

转账前后总和都是1500元，这就是事务的一致性。


### 隔离性

当多个事务同时操作一张表时互不干扰相互隔离，关于事务的隔离数据库提供了多种隔离级别，详情点击[事务的隔离性](./transaction.md#Isolation)

### 持久性

事务一旦提交后，数据库中数据的改变也是永久的，即使在提交前数据库故障了也不会丢失这个操作。

例如：

用户在前台注册账号，提交事务后提示用户注册成功，如果数据库丢失了事务提交那将造成重大失误，这是不允许的。

## <div id="program"></div>使用spring控制事务（编程式），以转账为例

### [示例项目](./spring-framework-demo/transaction-program)

### 依赖包

```
<!-- 事务 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>${spring.version}</version>
</dependency>
```

### <div id="PlatformTransactionManager"></div>PlatformTransactionManager接口类（命令式事务管理器中心接口类）

类图：

![](./images/PlatformTransactionManager.png)

#### TransactionDefinition类

用来定义事务的一些属性的接口，例如事务的传播行为、隔离性、超时时间、等等

#### TransactionStatus 类

存储事务状态的接口

#### 命令式事务管理器实现类

- DataSourceTransactionManager：jdbc的事务管理器
- JtaTransactionManager：分布式事务管理器
- HibernateTransactionManager:Hibernate事务管理器
- JpaTransactionManager:jpa事务管理器

多说一句**mybatis是自己处理的事务**没有实现PlatformTransactionManager接口（**有疑问，表述不太正确**）


### 配置jdbc事务管理器

```
@Bean 
public DataSourceTransactionManager transactionManager(@Qualifier("druidDataSource") DataSource dataSource){
    DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
    dataSourceTransactionManager.setDataSource(dataSource);
    return dataSourceTransactionManager;
}
```

### 数据库表结构

#### account表
|列名|类型|含义|
|---|---|---|
|id|bigint|主键|
|user_name|varchar(50)|姓名|
|money|bigint|账户金额|

### 没有事务的情况下抛出异常

由于没有事务，执行的sql将实时入库不可回滚，所以遇到异常，异常前的sql正常执行，异常后的sql不再执行，这样就丢失了一致性

#### 关键代码

```
public boolean transferAccountsNotTransaction(Long formId, Long toId, Long money) {

    try{
        // form 账户减掉200
        AccountChangeDTO accountChangeFrom = new AccountChangeDTO();
        accountChangeFrom.setId(formId);
        accountChangeFrom.setChangemoney(-money);
        int row = accountDao.moneyChange(accountChangeFrom);
        if(row <= 0){
            throw new Exception("转账来源错误");
        }

        /** 制造异常 */
        int num = 1/0;

        // to 账户增加200
        AccountChangeDTO accountChangeTo = new AccountChangeDTO();
        accountChangeFrom.setId(formId);
        accountChangeFrom.setChangemoney(money);
        row = accountDao.moneyChange(accountChangeTo);
        if(row <= 0){
            throw new Exception("转账接收方错误");
        }
    } catch (Exception e){
        e.printStackTrace();
        return false;
    }

    return true;
}
```

#### 测试用例

```
Long zhangsangId;
Long lisiId;
@Test
void transferAccountsNotTransaction() {
    boolean flag = accountService.transferAccountsNotTransaction(zhangsangId,lisiId,200L);
    Assertions.assertEquals(flag,false);

    //初始金额为1000
    Account zhangsan = accountService.queryById(zhangsangId);
    Assertions.assertEquals(zhangsan.getMoney(),800L);

    //由于异常接收方的金额变动还没有执行，所以金额还是500
    Account lisi = accountService.queryById(lisiId);
    Assertions.assertEquals(lisi.getMoney(),500L);
}
```

#### 最终数据库数据
id|user_name|money
----|:----:|:----:
29|张三|800
30|张三|500

一开始两人总共1800元，最终由于异常 张三 金额变动成功，李四 金额变动未执行，导致最后两个人总共1600元，200元不翼而飞。这种情况是不允许的，需要使用事务来解决。

### 使用事务时抛出异常

当有事务时遇到异常可以回滚数据，避免了上边的问题。

#### 关键代码

```
public boolean transferAccountsTransaction(Long formId, Long toId, Long money) {
    //设置事务的属性（隔离级别、传播行为、超时时间、等）
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    //开启事务
    TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

    try{
        // form 账户减掉 money 
        AccountChangeDTO accountChangeFrom = new AccountChangeDTO();
        accountChangeFrom.setId(formId);
        accountChangeFrom.setChangemoney(-money);
        int row = accountDao.moneyChange(accountChangeFrom);
        if(row <= 0){
            throw new Exception("转账来源错误");
        }

        /** 制造异常 */
        int num = 1/0;

        // to 账户增加 money 
        AccountChangeDTO accountChangeTo = new AccountChangeDTO();
        accountChangeFrom.setId(formId);
        accountChangeFrom.setChangemoney(money);
        row = accountDao.moneyChange(accountChangeTo);
        if(row <= 0){
            throw new Exception("转账接收方错误");
        }
    } catch (Exception e){
        e.printStackTrace();
        platformTransactionManager.rollback(transactionStatus);
        return false;
    }

    platformTransactionManager.commit(transactionStatus);
    return true;

}
```

#### 测试用例

```
@Test
void transferAccountsTransaction() {
    boolean flag = accountService.transferAccountsTransaction(zhangsangId,lisiId,200L);
    Assertions.assertEquals(flag,false);

    //初始金额为1000 事务回滚后金额不变
    Account zhangsan = accountService.queryById(zhangsangId);
    Assertions.assertEquals(zhangsan.getMoney(),1000L);

    //由于异常接收方的金额变动还没有执行，即使执行了因为异常也会回滚，所以金额还是500
    Account lisi = accountService.queryById(lisiId);
    Assertions.assertEquals(lisi.getMoney(),500L);
}
```

#### 最终数据库数据

id|user_name|money
----|:----:|:----:
31|张三|1000
32|李四|500

由于添加了事务，遇到异常后回滚了数据，所有张三扣除的200元并没有提交到数据库，不影响数据库中的数据。

## <div id="statement"></div>声明式事务（注解方式）

### [示例项目](./spring-framework-demo/transaction-statement)

### 开启注解式事务

在配置类上添加 `@EnableTransactionManagement` 注解

示例：

```
@Configuration
//开启注解式事务
@EnableTransactionManagement
public class SpringConf {
}
```

### 使用

在方法上添加 `@Transactional` 注解开启事务

示例：

```
@Transactional
public boolean transferAccountsTransaction(Long formId, Long toId, Long money) throws Exception {

    // form 账户减掉 money
    AccountChangeDTO accountChangeFrom = new AccountChangeDTO();
    accountChangeFrom.setId(formId);
    accountChangeFrom.setChangemoney(-money);
    int row = accountDao.moneyChange(accountChangeFrom);
    if(row <= 0){
        throw new Exception("转账来源错误");
    }

    /** 制造异常 */
    int num = 1/0;

    // to 账户增加 money
    AccountChangeDTO accountChangeTo = new AccountChangeDTO();
    accountChangeFrom.setId(formId);
    accountChangeFrom.setChangemoney(money);
    row = accountDao.moneyChange(accountChangeTo);
    if(row <= 0){
        throw new Exception("转账接收方错误");
    }
        
    return true;

}
```

### 原理

声明式事务通过aop进行切面开启事务、回滚事务、提交事务，部分核心代码：

```

public abstract class TransactionAspectSupport implements BeanFactoryAware, InitializingBean {
	protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass,
			final InvocationCallback invocation) throws Throwable {

		...

		TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);

		Object retVal;
		try {
			//执行代理方法
			retVal = invocation.proceedWithInvocation();
		} catch (Throwable ex) {
			// 回滚
			completeTransactionAfterThrowing(txInfo, ex);
			throw ex;
		}

		...

		//提交
		commitTransactionAfterReturning(txInfo);
		return retVal;

		...

	}
}
```

所以声明式事务遇到异常会自动回滚

## <div id="Isolation"></div>事务的隔离性

### 什么是隔离性
将同时运行的多个事务之间的操作进行隔离

### 没有隔离性会导致什么问题

会导致以下三个问题

- **脏读**：事务A中读取到事务B中修改的数据，事务B又回滚了数据

同一账户转账+存款示例:

|时间|转账事务A|存款事务B|
|:----:|:----:|:----:|
|T1|开始事务|-|
|T2|-|开始事务|
|T3|查询账户余额为1000元|-|
|T4|转出200元将余额修改为800元|-|
|T5|-|查询账户余额为800元（脏读）|
|T6|-|存款100元将余额修改为900元|
|T7|异常导致回滚事务，余额变回1000元|-|
|T8|-|提交事务，余额变为900|

以上示例，由于事务B读取了事务A更新的数据，然后事务A回滚，那么事务B读取到的就是脏数据。

- **幻读**（侧重于添加和删除）：由于事务B中**添加或删除**了记录，导致事务A前后查询的数据不一致

用户统计与注册示例:

|时间|统计事务A|注册事务B|
|:----:|:----:|:----:|
|T1|开始事务|-|
|T2|-|开始事务|
|T6|再次统计用户为79个|-|
|T3|-|注册一个用户|
|T5|-|提交事务|
|T4|统计用户数为80个（幻读）|-|

以上示例中，由于事务B的新增数据，导致事务A前后统计的数据不一致，像发生了幻觉。

- **不可重复读**（侧重于修改）：事务中对同一条数据读取了两次，但是前后读取的不一致；由于事务B中**修改**了记录，导致事务A前后查询的数据不一致

同一账户转账+存款示例:

|时间|转账事务A|存款事务B|
|:----:|:----:|:----:|
|T1|-|开始事务|
|T2|开始事务|-|
|T3|-|查询账户余额为1000元|
|T3|查询账户余额为1000元|-|
|T4|-|转出200元将余额修改为800元|
|T7|-|提交事务，余额变为800元|
|T5|查询账户余额为800元（不可重复读）|-|

以上示例中，由于事务B的修改数据，导致事务A前后查询的同一条数据不一致。

**注意：幻读侧重于添加和删除，不可重复读侧重于修改**


### 4种隔离性
|事务隔离级别|描述|脏读|不可重复读|幻读|
|----|:----:|:----:|:----:|:----:|
|读未提交（Read Uncommitted）|一个事务可以读取另一个未提交事务的数据|是|是|是|
|读已提交（Read Committed）|若有事务对数据进行**更新**（UPDATE）操作时，读操作事务要等待这个更新操作**事务提交后**才能读取数据|否|是|是|
|可重复读（Repeatable Read）|开始读取数据（事务开启）时，**不再允许修改操作**（其他事务修改会引起“不可重复读”的情况）|否|否|是|
|串行化（Serializable）|事务串行化顺序执行|否|否|否|



###  在 `@Transactional` 中指定隔离性

isolation属性是指定隔离性，其类型为`Isolation`，默认值为`Isolation.DEFAULT`这个值是数据库中对事务的默认值，示例指定为serializable

```
@Transactional(isolation = Isolation.SERIALIZABLE)
```

**大多数数据库默认的事务隔离级别是Read committed，比如Sql Server , Oracle。Mysql的默认隔离级别是Repeatable read。**

## <div id="propagation"></div>事务的传播行为

### 什么是传播行为

管理多个事务之间调用的过程

### 事务的7个传播行为

#### 描述
|传播属性||描述|速记|
|:----|:----|-----|-----|
|REQUIRED|确保自己有事务|如果有事务在运行，当前的方法就在这个事务内运行，否则，就启动一个新的事务，并在自己的事务内运行|更关注本身事务|
|SUPPORTS|可以不使用事务|如果有事务在运行，当前的方法就在这个事务内运行，否则**它可以不运行在事务中**|更关注本身事务|
|NOT_SUPPORTED|强制不使用事务|当前的方法不应该运行在事务中，如果**有运行的事务，将它挂起**|更关注本身事务|
|MANDATORY|调用方必须有事务|当前的方法**必须运行在事务内部**，如果没有正在运行的事务，就抛出异常|关注调用方事务|
|REQUIRES_NEW|必须使用新事务|当前的方法必须启动新事务，并在它自己的事务内运行，如果有事务正在运行，应该将它挂起|关注调用方事务|
|NEVER|不允许在事务中运行|当前的方法不应该运行在事务中，如果**有运行的事务，就抛出异常**|关注调用方事务|
|NESTED|嵌套事务|如果有事务在运行，当前的方法就应该在这个事务的嵌套事务内运行，否则，就启动一个新的事务，并在它自己的事务内运行||

⭐**通过速记列，区分传播行为的侧重方，分为两大类：更关注本身事务 和 关注调用方事务**

#### 使用表格比较不同

A方法内调用B方法

|-|REQUIRED（确保自己有事务）|MANDATORY(调用方必须有事务)|REQUIRES_NEW(必须使用新事务)|SUPPORTS（可以不使用事务）|NOT_SUPPORTED(强制不使用事务)|NEVER(不允许在事务中运行)|NESTED（嵌套事务）|
|:----|:----|:----|:----|:----|:----|:----|:----|
|A方法有事务|B方法使用A方法的事务|B方法使用A方法的事务|A方法的事务挂起,B方法开启新事务|B方法使用A方法的事务|A方法的事务挂起|抛出异常|B方法在A方法事务的嵌套事务中运行|
|A方法无事务|B方法开启新事务|抛出异常|B方法开启新事务|B方法不使用事务|B方法不使用事务|B方法不使用事务|B方法开启新事务|

#### 在 `@Transactional` 中指定传播方式

propagation属性指定传播方式其类型为Propagation(枚举类)，默认值为 REQUIRED ，示例(指定为REQUIRES_NEW)：

```
@Transactional(propagation = Propagation.REQUIRES_NEW)
```

### [示例项目](./spring-framework-demo/transaction-propagation)

#### ⭐REQUIRES_NEW 与 NESTED 的不同


- notansactionPropagationByNested测试用例 与 notansactionAndPropagationByRequiresNew测试用例 结果一致
- tansactionNestedAndRequiredNewDifferentByRequiresNew测试用例 与 tansactionNestedAndRequiredNewDifferentByNested测试用例 结果一致
- tansactionPropagationByNested测试用例 与 tansactionAndPropagationByRequiresNew测试用例 结果不一致，因为是在父方法抛出异常
- 总结：
	- RequiresNew的两个事务并不相关
	- **Nested的事务是嵌套的，主事务回滚嵌套的事务也回滚，嵌套事务回滚主事务不回滚**

## 事务的其他属性

### timeout

设置事务的超时时间，默认为 -1 不超时，单位:秒。

`@Transactional`中指定超时时间，示例：

```
@Transactional(timeout=20)
```

### rollbackFor

设置事务在什么异常下进行回滚

`@Transactional`中指定异常回滚，示例Exception异常回滚：

```
@Transactional(rollbackFor = Exception.class)
```

### noRollbackFor

设置事务在什么异常下不回滚

`@Transactional`中指定异常不回滚，示例RuntimeException异常不回滚：

```
@Transactional(noRollbackFor = RuntimeException.class)
```

