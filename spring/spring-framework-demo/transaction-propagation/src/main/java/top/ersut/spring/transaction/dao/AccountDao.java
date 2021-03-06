package top.ersut.spring.transaction.dao;

import top.ersut.spring.transaction.pojo.Account;

public interface AccountDao {

    public Long insertBackId(Account account);

    /** 删除所有数据 */
    public Integer deleteAll();

    /** 根据id查询 */
    public Account selectById(Long id);


}
