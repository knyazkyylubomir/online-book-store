package org.project.name.online.book.store.repository;

import org.project.name.online.book.store.model.Book;

import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List findAll();
}
