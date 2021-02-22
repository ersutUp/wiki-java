package top.ersut.spring.ioc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Collection {

    private String[] array;

    private List<String> list;

    private Set<String> set;

    private Map<String,String> map;

    public void setArray(String[] array) {
        this.array = array;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setSet(Set<String> set) {
        this.set = set;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void print(){
        System.out.println("array:"+ Arrays.toString(array));
        System.out.println("list:"+list);
        System.out.println("set:"+set);
        System.out.println("map:"+map);
    }

}
