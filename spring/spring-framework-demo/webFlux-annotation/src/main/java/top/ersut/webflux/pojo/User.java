package top.ersut.webflux.pojo;

public class User {

    private Long ID;

    private String name;

    public User() {}

    public User(String name) {
        this.name = name;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
