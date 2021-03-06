package top.ersut.spring.transaction.pojo;

public class Account {

    private Long id;

    /** 姓名 */
    private String userName;

    /** 账户金额 */
    private Long money;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }
}
