package top.ersut.spring.junit5.service.impl;

import org.springframework.stereotype.Service;
import top.ersut.spring.junit5.pojo.Book;
import top.ersut.spring.junit5.service.BookService;

import java.util.HashMap;
import java.util.Map;


@Service
public class BookServiceiImpl implements BookService {


    final static Map<Long,Book> BOOK_MAP = new HashMap<>();

    @Override
    public void save(Book book) {
        if(book != null && book.getId() != null){
            BOOK_MAP.put(book.getId(),book);
        }
    }

    @Override
    public Book query(Long id) {
        return BOOK_MAP.get(id);
    }
}
