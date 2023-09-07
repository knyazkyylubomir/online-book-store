package org.project.name.online.book.store.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.dto.BookDto;
import org.project.name.online.book.store.dto.CreateBookRequestDto;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.mapper.BookMapper;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.repository.BookRepository;
import org.project.name.online.book.store.service.BookService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        Book book = bookMapper.toModel(bookDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findBookById(id).orElseThrow(
                () -> new EntityNotFoundException("There are no book by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
