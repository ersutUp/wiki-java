package top.ersut.spring.transaction.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.SpringConf;
import top.ersut.spring.transaction.pojo.Account;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    AccountService accountService;

    Long zhangsangId;
    Long lisiId;

    @BeforeEach
    public void before(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConf.class);
        accountService = applicationContext.getBean(AccountService.class);
        Assertions.assertNotNull(accountService);

        //初始化数据库
        //删除旧数据
        accountService.deleteAll();
        //创建两个个账户
        Account zhangsang = new Account();
        zhangsang.setMoney(1000L);
        zhangsang.setUserName("张三");
        zhangsangId = accountService.saveBackId(zhangsang);
        Assertions.assertNotNull(zhangsangId);

        Account lisi = new Account();
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisiId = accountService.saveBackId(lisi);
        Assertions.assertNotNull(lisiId);

    }

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

    @Test
    void transferAccountsTransaction() throws Exception {
        boolean flag = accountService.transferAccountsTransaction(zhangsangId,lisiId,200L);
        Assertions.assertEquals(flag,false);

        //初始金额为1000 事务回滚后金额不变
        Account zhangsan = accountService.queryById(zhangsangId);
        Assertions.assertEquals(zhangsan.getMoney(),1000L);

        //由于异常接收方的金额变动还没有执行，即使执行了因为异常也会回滚，所以金额还是500
        Account lisi = accountService.queryById(lisiId);
        Assertions.assertEquals(lisi.getMoney(),500L);
    }
}