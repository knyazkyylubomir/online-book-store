package org.project.name.online.book.store.service;


import org.project.name.online.book.store.model.Book;

import java.util.List;

public interface BookService {
    Book save(Book book);

    List findAll();
}
