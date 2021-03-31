package top.ersut.boot.yml;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "people")
public class People {

    private String name;

    private String alias;

    private String sex;

    private Integer age;

    /**交通工具*/
    private Map<String,Integer> transport;

    private List<String> books;

    private Set<People> friends;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Set<People> getFriends() {
        return friends;
    }

    public void setFriends(Set<People> friends) {
        this.friends = friends;
    }

    public Map<String, Integer> getTransport() {
        return transport;
    }

    public void setTransport(Map<String, Integer> transport) {
        this.transport = transport;
    }

    public List<String> getBooks() {
        return books;
    }

    public void setBooks(List<String> books) {
        this.books = books;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
