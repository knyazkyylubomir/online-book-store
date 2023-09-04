package org.project.name.online.book.store.repository;

import java.util.List;
import org.project.name.online.book.store.model.Book;

public interface BookRepository {
    Book save(Book book);

    List findAll();
}
