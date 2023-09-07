package org.project.name.online.book.store.service;

import java.util.List;
import org.project.name.online.book.store.dto.BookDto;
import org.project.name.online.book.store.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto bookDto);

    BookDto getBookById(Long id);

    List<BookDto> findAll();
}
