package top.ersut.spring.jdbc.service;

public interface BookService {

    int save(String name,String price);

    Long saveBackId(String name,String price);

    int change(Long id,String name,String price);

    int remove(Long id);

}
