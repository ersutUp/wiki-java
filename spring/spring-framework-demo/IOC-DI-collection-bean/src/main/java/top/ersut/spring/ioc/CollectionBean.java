package top.ersut.spring.ioc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionBean {

    private User[] array;

    private List<User> list;

    private Set<User> set;

    private Map<User,User> map;

    public void setArray(User[] array) {
        this.array = array;
    }

    public void setList(List<User> list) {
        this.list = list;
    }

    public void setSet(Set<User> set) {
        this.set = set;
    }

    public void setMap(Map<User, User> map) {
        this.map = map;
    }

    public void print(){
        System.out.println("array:"+ Arrays.toString(array));
        System.out.println("list:"+list);
        System.out.println("set:"+set);
        System.out.println("map:"+map);
    }

}
