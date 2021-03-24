package top.ersut.spring.transaction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import top.ersut.spring.transaction.dao.AccountDao;
import top.ersut.spring.transaction.pojo.Account;
import top.ersut.spring.transaction.pojo.AccountDTO;
import top.ersut.spring.transaction.service.AccountService;

import java.util.Objects;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AccountService accountService;

    @Override
    public Long saveBackId(Account account) {
        return accountDao.insertBackId(account);
    }

    /**
     * 删除所有数据
     */
    @Override
    public Integer deleteAll() {
        return accountDao.deleteAll();
    }

    /**
     * 根据id获取
     *
     * @param id
     */
    @Override
    public Account queryById(Long id) {
        return accountDao.selectById(id);
    }
    
    /**
     * 没有事务的A方法
     *
     * @param propagation
     */
    @Override
    public void notansaction(Propagation propagation,AccountDTO accountDTO) {
        //创建张三的账户
        Account zhangsan = new Account();
        zhangsan.setMoney(1000L);
        zhangsan.setUserName("张三");
        zhangsan.setId(accountDao.insertBackId(zhangsan));
        accountDTO.setZhangsan(zhangsan);

        Account lisi = new Account();
        accountDTO.setLisi(lisi);
        //不同事务传播方式创建李四账户
        if (Objects.equals(propagation,Propagation.REQUIRED)){
            /*
            注意:这里一定要用注入的 accountService 才能将事务生效。
            由于当前声明式事务使用 AOP（jdk动态代理） 实现，如果使用 this 那调用的非AOP代理类，注入的 accountService 才是AOP代理类。
            当然如果是 CGLIB动态代理 实现,则不存在这个问题。
            可以在@EnableTransactionManagement 注解的 mode 属性上指定动态代理模式
            注意：CGLIB模式，需使用 -javaagent: 指定 aspectjweaver.jar 文件
             */
            accountService.propagationByRequiredError(lisi);
        } else if (Objects.equals(propagation,Propagation.MANDATORY)){
            accountService.propagationByMandatoryError(lisi);
        } else if (Objects.equals(propagation,Propagation.REQUIRES_NEW)){
            accountService.propagationByRequiresNewError(lisi);
        } else if (Objects.equals(propagation,Propagation.SUPPORTS)){
            accountService.propagationBySupportsError(lisi);
        } else if (Objects.equals(propagation,Propagation.NOT_SUPPORTED)){
            accountService.propagationByNotSupportedError(lisi);
        } else if (Objects.equals(propagation,Propagation.NEVER)){
            accountService.propagationByNeverError(lisi);
        } else if (Objects.equals(propagation,Propagation.NESTED)){
            accountService.propagationByNestedError(lisi);
        }
    }

    /**
     * 有事务的A方法
     *
     * @param propagation
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void tansaction(Propagation propagation,AccountDTO accountDTO) {
        //创建张三的账户
        Account zhangsan = new Account();
        zhangsan.setMoney(1000L);
        zhangsan.setUserName("张三");
        zhangsan.setId(accountDao.insertBackId(zhangsan));
        accountDTO.setZhangsan(zhangsan);

        Account lisi = new Account();
        accountDTO.setLisi(lisi);
        //不同事务传播方式创建李四账户
        if (Objects.equals(propagation,Propagation.REQUIRED)){
            accountService.propagationByRequired(lisi);
        } else if (Objects.equals(propagation,Propagation.MANDATORY)){
            accountService.propagationByMandatory(lisi);
        } else if (Objects.equals(propagation,Propagation.REQUIRES_NEW)){
            accountService.propagationByRequiresNew(lisi);
        } else if (Objects.equals(propagation,Propagation.SUPPORTS)){
            accountService.propagationBySupports(lisi);
        } else if (Objects.equals(propagation,Propagation.NOT_SUPPORTED)){
            accountService.propagationByNotSupported(lisi);
        } else if (Objects.equals(propagation,Propagation.NEVER)){
            accountService.propagationByNever(lisi);
        } else if (Objects.equals(propagation,Propagation.NESTED)){
            accountService.propagationByNested(lisi);
        }
        //制造异常
        int i = 1/0;
    }


    /** REQUIRES_NEW 与 NESTED 的不同 */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void tansactionNestedAndRequiredNewDifferent(Propagation propagation,AccountDTO accountDTO) {
        //创建张三的账户
        Account zhangsan = new Account();
        zhangsan.setMoney(1000L);
        zhangsan.setUserName("张三");
        zhangsan.setId(accountDao.insertBackId(zhangsan));
        accountDTO.setZhangsan(zhangsan);

        Account lisi = new Account();
        accountDTO.setLisi(lisi);
        //不同事务传播方式创建李四账户
        if (Objects.equals(propagation,Propagation.REQUIRES_NEW)){
            //进行try-catch 因为是不同事务 所以不能影响本方法的事务
            try {
                accountService.propagationByRequiresNewError(lisi);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else if (Objects.equals(propagation,Propagation.NESTED)){
            //进行try-catch 因为是不同事务 所以不能影响本方法的事务
            try {
                accountService.propagationByNestedError(lisi);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void propagationByRequired(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void propagationByRequiresNew(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void propagationBySupports(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void propagationByNotSupported(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void propagationByMandatory(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void propagationByNever(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void propagationByNested(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
    }




    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void propagationByRequiredError(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
        //制造异常
        int i = 1/0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void propagationByRequiresNewError(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
        //制造异常
        int i = 1/0;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void propagationBySupportsError(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
        //制造异常
        int i = 1/0;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void propagationByNotSupportedError(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
        //制造异常
        int i = 1/0;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void propagationByMandatoryError(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
        //制造异常
        int i = 1/0;
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void propagationByNeverError(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
        //制造异常
        int i = 1/0;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void propagationByNestedError(Account lisi){
        //创建李四的账户
        lisi.setMoney(500L);
        lisi.setUserName("李四");
        lisi.setId(accountDao.insertBackId(lisi));
        //制造异常
        int i = 1/0;
    }

}
