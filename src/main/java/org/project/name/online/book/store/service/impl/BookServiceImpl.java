package org.project.name.online.book.store.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.dto.BookDto;
import org.project.name.online.book.store.dto.BookSearchParameters;
import org.project.name.online.book.store.dto.CreateBookRequestDto;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.mapper.BookMapper;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.repository.book.BookRepository;
import org.project.name.online.book.store.repository.book.BookSpecificationBuilder;
import org.project.name.online.book.store.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        Book book = bookMapper.toModel(bookDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "There are no book by id: " + id));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto updateBookById(Long id, CreateBookRequestDto bookDto) {
        Book bookById = bookRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "There are no book by id: " + id));
        Book book = bookMapper.mergeEntities(bookDto, bookRepository.save(bookById));
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> searchBook(BookSearchParameters searchParameters) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
