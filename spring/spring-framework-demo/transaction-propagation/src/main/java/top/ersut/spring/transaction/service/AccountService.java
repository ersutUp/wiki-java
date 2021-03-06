package top.ersut.spring.transaction.service;

import org.springframework.transaction.annotation.Propagation;
import top.ersut.spring.transaction.pojo.Account;
import top.ersut.spring.transaction.pojo.AccountDTO;

public interface AccountService {

    public Long saveBackId(Account account);

    /** 删除所有数据 */
    public Integer deleteAll();

    /** 根据id获取 */
    public Account queryById(Long id);

    /** 没有事务的A方法 */
    void notansaction(Propagation propagation,AccountDTO accountDTO);

    /** 有事务的A方法 */
    void tansaction(Propagation propagation,AccountDTO accountDTO);

    /** REQUIRES_NEW 与 NESTED 的不同 */
    void tansactionNestedAndRequiredNewDifferent(Propagation propagation,AccountDTO accountDTO);

    void propagationByRequired(Account lisi);

    void propagationByNested(Account lisi);

    void propagationByNever(Account lisi);

    void propagationBySupports(Account lisi);

    void propagationByNotSupported(Account lisi);

    void propagationByRequiresNew(Account lisi);

    void propagationByMandatory(Account lisi);

    void propagationByRequiredError(Account lisi);

    void propagationByNestedError(Account lisi);

    void propagationByNeverError(Account lisi);

    void propagationBySupportsError(Account lisi);

    void propagationByNotSupportedError(Account lisi);

    void propagationByRequiresNewError(Account lisi);

    void propagationByMandatoryError(Account lisi);
}
