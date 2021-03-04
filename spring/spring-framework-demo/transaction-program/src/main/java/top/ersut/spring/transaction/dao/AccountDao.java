package top.ersut.spring.transaction.dao;

import top.ersut.spring.transaction.pojo.Account;
import top.ersut.spring.transaction.pojo.AccountChangeDTO;

public interface AccountDao {

    public Long insertBackId(Account account);

    /** 删除所有数据 */
    public Integer deleteAll();

    /** 根据id查询 */
    public Account selectById(Long id);

    /**
     * 金额变动
     * @param accountChangeDTO
     * @return
     */
    public int moneyChange(AccountChangeDTO accountChangeDTO);


}
