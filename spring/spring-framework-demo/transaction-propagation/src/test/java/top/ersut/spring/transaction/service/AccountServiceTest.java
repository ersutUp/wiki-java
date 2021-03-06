package top.ersut.spring.transaction.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import top.ersut.spring.config.SpringConf;
import top.ersut.spring.transaction.pojo.Account;
import top.ersut.spring.transaction.pojo.AccountDTO;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {


    AccountService accountService;

    @BeforeEach
    public void before(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConf.class);
        accountService = applicationContext.getBean(AccountService.class);
        Assertions.assertNotNull(accountService);

        //初始化数据库
        //删除旧数据
        accountService.deleteAll();
    }

    //REQUIRED 传播行为：B方法开启新事务
    @Test
    void notansactionAndPropagationByRequired() {

        AccountDTO accountDTO = new AccountDTO();

        /** 没有事务调用有事务 */
        try {
            accountService.notansaction(Propagation.REQUIRED,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //张三的数据 没有事务所以sql一经执行就持久化 肯定入库

        //李四的数据由于异常导致数据回滚,所以查询不到数据
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertNull(lisiDB);
    }
    //REQUIRED 传播行为：B方法使用A方法的事务
    @Test
    void tansactionAndPropagationByRequired() {

        AccountDTO accountDTO = new AccountDTO();

        /** 有事务调用有事务 */
        try{
            accountService.tansaction(Propagation.REQUIRED,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }
        //因为抛出了异常 所以张三的数据回滚了
        Account zhangsan = accountDTO.getZhangsan();
        Account zhangsanDB = accountService.queryById(zhangsan.getId());
        Assertions.assertNull(zhangsanDB);

        //李四与张三用的同一个事务，所以李四也回滚了
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertNull(lisiDB);

    }


    //MANDATORY 传播行为:抛出异常
    @Test
    void notansactionAndPropagationByMandatory() {
        AccountDTO accountDTO = new AccountDTO();

        /** 没有事务调用有事务 */
        try {
            accountService.notansaction(Propagation.MANDATORY,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //张三的数据 没有事务所以sql一经执行就持久化 肯定入库

        //由于Mandatory传播行为需要有事务环境否则报异常，所以李四没有数据
        Account lisi = accountDTO.getLisi();
        Assertions.assertNull(lisi.getId());
    }
    //MANDATORY 传播行为:B方法使用A方法的事务
    @Test
    void tansactionAndPropagationByMandatory() {
        AccountDTO accountDTO = new AccountDTO();

        /** 有事务调用有事务 */
        try {
            accountService.tansaction(Propagation.MANDATORY,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //因为抛出了异常 所以张三的数据回滚了
        Account zhangsan = accountDTO.getZhangsan();
        Account zhangsanDB = accountService.queryById(zhangsan.getId());
        Assertions.assertNull(zhangsanDB);

        //李四与张三用的同一个事务，所以李四也回滚了
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertNull(lisiDB);
    }


    //REQUIRES_NEW 传播行为：A方法的事务挂起,B方法开启新事务
    @Test
    void tansactionAndPropagationByRequiresNew() {
        AccountDTO accountDTO = new AccountDTO();

        try {
            accountService.tansaction(Propagation.REQUIRES_NEW,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //张三的数据由于异常导致数据回滚,所以查询不到数据
        Account zhangsan = accountDTO.getZhangsan();
        Account zhangsanDB = accountService.queryById(zhangsan.getId());
        Assertions.assertNull(zhangsanDB);

        //由于是 李四方法的事务是 REQUIRES_NEW 传播行为，两个事务并不影响，所以李四的数据成功入库
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertEquals(lisiDB.getMoney(),lisi.getMoney());

    }
    //REQUIRES_NEW 传播行为：B方法开启新事务
    @Test
    void notansactionAndPropagationByRequiresNew() {
        AccountDTO accountDTO = new AccountDTO();

        try {
            accountService.notansaction(Propagation.REQUIRES_NEW,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //张三的数据 没有事务所以sql一经执行就持久化 肯定入库

        //李四的数据由于异常导致数据回滚,所以查询不到数据
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertNull(lisiDB);
    }


    //SUPPORTS 传播行为：B方法不使用事务
    @Test
    void notansactionAndPropagationBySupports() {

        AccountDTO accountDTO = new AccountDTO();

        try {
            accountService.notansaction(Propagation.SUPPORTS,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //张三的数据 没有事务所以sql一经执行就持久化 肯定入库

        //李四方法的事务是 SUPPORTS 传播行为,因为张三的方法没有事务所以李四也没有事务，那么李四成功入库
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertEquals(lisiDB.getMoney(),lisi.getMoney());
    }
    //SUPPORTS 传播行为：B方法使用A方法的事务
    @Test
    void tansactionAndPropagationBySupports() {
        AccountDTO accountDTO = new AccountDTO();

        try {
            accountService.tansaction(Propagation.SUPPORTS,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //因为抛出了异常 所以张三的数据回滚了
        Account zhangsan = accountDTO.getZhangsan();
        Account zhangsanDB = accountService.queryById(zhangsan.getId());
        Assertions.assertNull(zhangsanDB);

        //李四与张三用的同一个事务，所以李四也回滚了
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertNull(lisiDB);
    }


    //NOT_SUPPORTED 传播行为：B方法不使用事务
    @Test
    void notansactionAndPropagationByNotSupported() {
        AccountDTO accountDTO = new AccountDTO();

        try {
            accountService.notansaction(Propagation.NOT_SUPPORTED,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //张三的数据 没有事务所以sql一经执行就持久化 肯定入库

        //李四方法的事务是 NOT_SUPPORTED 传播行为,所以李四没有事务，那么李四成功入库
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertEquals(lisiDB.getMoney(),lisi.getMoney());
    }
    //NOT_SUPPORTED 传播行为：A方法的事务挂起
    @Test
    void tansactionAndPropagationByNotSupported() {
        AccountDTO accountDTO = new AccountDTO();

        try {
            accountService.tansaction(Propagation.NOT_SUPPORTED,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //因为抛出了异常 所以张三的数据回滚了
        Account zhangsan = accountDTO.getZhangsan();
        Account zhangsanDB = accountService.queryById(zhangsan.getId());
        Assertions.assertNull(zhangsanDB);

        //李四方法的事务是 NOT_SUPPORTED 传播行为,所以李四没有事务，那么李四成功入库
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertEquals(lisiDB.getMoney(),lisi.getMoney());
    }


    //NEVER 传播行为：B方法不使用事务
    @Test
    void notansactionAndPropagationByNever() {
        AccountDTO accountDTO = new AccountDTO();

        try {
            accountService.notansaction(Propagation.NEVER,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //张三的数据 没有事务所以sql一经执行就持久化 肯定入库

        //李四方法的事务是 NEVER 传播行为且张三没有事务,所以李四没有事务，那么李四成功入库
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertEquals(lisiDB.getMoney(),lisi.getMoney());
    }
    //NEVER 传播行为：抛出异常
    @Test
    void tansactionAndPropagationByNever() {
        AccountDTO accountDTO = new AccountDTO();

        try {
            accountService.tansaction(Propagation.NEVER,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //因为抛出了异常 所以张三的数据回滚了
        Account zhangsan = accountDTO.getZhangsan();
        Account zhangsanDB = accountService.queryById(zhangsan.getId());
        Assertions.assertNull(zhangsanDB);

        //李四方法的事务是 NEVER 传播行为且张三有事务,所以李四抛出异常
        Account lisi = accountDTO.getLisi();
        Assertions.assertNull(lisi.getId());
    }


    //NESTED 传播行为：B方法在A方法事务的嵌套事务中运行
    @Test
    void tansactionPropagationByNested() {
        AccountDTO accountDTO = new AccountDTO();

        /** 有事务调用有事务 */
        try{
            accountService.tansaction(Propagation.NESTED,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }
        //因为抛出了异常 所以张三的数据回滚了
        Account zhangsan = accountDTO.getZhangsan();
        Account zhangsanDB = accountService.queryById(zhangsan.getId());
        Assertions.assertNull(zhangsanDB);

        //李四与张三用的同一个事务，所以李四也回滚了
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertNull(lisiDB);
    }
    //NESTED 传播行为：B方法开启新事务
    @Test
    void notansactionPropagationByNested() {
        AccountDTO accountDTO = new AccountDTO();

        /** 没有事务调用有事务 */
        try {
            accountService.notansaction(Propagation.NESTED,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }

        //张三的数据 没有事务所以sql一经执行就持久化 肯定入库

        //李四的数据由于异常导致数据回滚,所以查询不到数据
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertNull(lisiDB);
    }


    /**
     * REQUIRES_NEW 与 NESTED 的不同
     * notansactionPropagationByNested 与 notansactionAndPropagationByRequiresNew 结果一致，因为开启了新事务
     * tansactionPropagationByNested 与 tansactionAndPropagationByRequiresNew 结果不一致，因为是在父方法抛出异常，而RequiresNew的两个事务并不相关，Nested的事务是嵌套的，主事务回滚嵌套的事务也回滚
     * tansactionNestedAndRequiredNewDifferentByRequiresNew 与 tansactionNestedAndRequiredNewDifferentByNested 结果一致，因为他们是在子方法抛出的异常
     * 当父方法有事务在子方法抛出异常那么 REQUIRES_NEW 与 NESTED 的结果就不同了
     */
    //REQUIRES_NEW
    @Test
    void tansactionNestedAndRequiredNewDifferentByRequiresNew() {
        AccountDTO accountDTO = new AccountDTO();

        /** 有事务调用有事务 */
        try{
            accountService.tansactionNestedAndRequiredNewDifferent(Propagation.REQUIRES_NEW,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }
        //李四的方法抛出了异常，所以李四回滚了
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertNull(lisiDB);

        //因为是不同的事务 所以张三的数据正常提交
        Account zhangsan = accountDTO.getZhangsan();
        Account zhangsanDB = accountService.queryById(zhangsan.getId());
        Assertions.assertEquals(zhangsanDB.getMoney(),zhangsan.getMoney());

    }
    //NESTED
    @Test
    void tansactionNestedAndRequiredNewDifferentByNested() {
        AccountDTO accountDTO = new AccountDTO();

        /** 有事务调用有事务 */
        try{
            accountService.tansactionNestedAndRequiredNewDifferent(Propagation.NESTED,accountDTO);
        } catch (Exception e){
            e.printStackTrace();
        }
        //李四的方法抛出了异常，所以李四回滚了
        Account lisi = accountDTO.getLisi();
        Account lisiDB = accountService.queryById(lisi.getId());
        Assertions.assertNull(lisiDB);

        //因为 李四的方法 是嵌套事务 所以张三的数据不会回滚
        Account zhangsan = accountDTO.getZhangsan();
        Account zhangsanDB = accountService.queryById(zhangsan.getId());
        Assertions.assertEquals(zhangsanDB.getMoney(),zhangsan.getMoney());

    }



}