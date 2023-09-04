package org.project.name.online.book.store.service;

import java.util.List;
import org.project.name.online.book.store.model.Book;

public interface BookService {
    Book save(Book book);

    List findAll();
}
