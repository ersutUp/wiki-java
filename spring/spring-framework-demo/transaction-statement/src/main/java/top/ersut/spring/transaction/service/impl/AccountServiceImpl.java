package top.ersut.spring.transaction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import top.ersut.spring.transaction.dao.AccountDao;
import top.ersut.spring.transaction.pojo.Account;
import top.ersut.spring.transaction.pojo.AccountChangeDTO;
import top.ersut.spring.transaction.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;

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

    @Override
    public boolean transferAccountsNotTransaction(Long formId, Long toId, Long money) {
        if(!(formId != null && formId != 0)){
            return false;
        }
        if(!(toId != null && toId != 0)){
            return false;
        }
        if(!(money != null && money > 0)){
            return false;
        }

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
            return false;
        }

        return true;
    }

    /**
     * 转账有事务
     *
     * @param formId
     * @param toId
     * @param money
     */
    @Override
    @Transactional
    public boolean transferAccountsTransaction(Long formId, Long toId, Long money) throws Exception {

        if(!(formId != null && formId != 0)){
            return false;
        }
        if(!(toId != null && toId != 0)){
            return false;
        }
        if(!(money != null && money > 0)){
            return false;
        }

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

}
