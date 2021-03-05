package top.ersut.spring.transaction.service;

import top.ersut.spring.transaction.pojo.Account;

public interface AccountService {

    public Long saveBackId(Account account);

    /** 删除所有数据 */
    public Integer deleteAll();

    /** 根据id获取 */
    public Account queryById(Long id);

    /** 转账没有事务 */
    public boolean transferAccountsNotTransaction(Long formId, Long toId, Long money);

    /** 转账有事务 */
    public boolean transferAccountsTransaction(Long formId, Long toId, Long money) throws Exception;

}
