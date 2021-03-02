package top.ersut.spring.jdbc.pojo;

public class BookConstructor {

    public BookConstructor(Long id, String bName, String price) {
        this.id = id;
        this.bName = bName;
        this.price = price;
    }

    /** 主键 */
    private Long id;

    /** 书名 */
    private String bName;

    /** 价格 */
    private String price;

    public Long getId() {
        return id;
    }

    public String getbName() {
        return bName;
    }

    public String getPrice() {
        return price;
    }
}
