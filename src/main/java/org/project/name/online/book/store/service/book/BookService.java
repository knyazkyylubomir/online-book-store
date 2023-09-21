package org.project.name.online.book.store.service.book;

import java.util.List;
import org.project.name.online.book.store.dto.book.BookDto;
import org.project.name.online.book.store.dto.book.BookSearchParameters;
import org.project.name.online.book.store.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto bookDto);

    BookDto getBookById(Long id);

    List<BookDto> findAll(Pageable pageable);

    BookDto updateBookById(Long id, CreateBookRequestDto bookDto);

    List<BookDto> searchBook(BookSearchParameters searchParameters);

    void deleteById(Long id);
}
