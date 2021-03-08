package top.ersut.spring.junit5.pojo;

public class Book {

    /** 主键 */
    private Long id;

    /** 书名 */
    private String bName;

    /** 价格 */
    private String price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
