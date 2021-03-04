package top.ersut.spring.transaction.pojo;

public class AccountChangeDTO {

    private Long id;

    /** 支出或收入的金额 */
    private Long changemoney;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChangemoney() {
        return changemoney;
    }

    public void setChangemoney(Long changemoney) {
        this.changemoney = changemoney;
    }
}
