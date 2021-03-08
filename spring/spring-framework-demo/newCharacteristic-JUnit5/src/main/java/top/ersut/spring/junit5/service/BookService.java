package top.ersut.spring.junit5.service;

import top.ersut.spring.junit5.pojo.Book;

public interface BookService {

    void save(Book book);

    Book query(Long id);

}
