package org.project.name.online.book.store.service.book.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.dto.book.BookDto;
import org.project.name.online.book.store.dto.book.BookDtoWithoutCategoryIds;
import org.project.name.online.book.store.dto.book.BookSearchParameters;
import org.project.name.online.book.store.dto.book.CreateBookRequestDto;
import org.project.name.online.book.store.dto.book.UpdateBookRequestDto;
import org.project.name.online.book.store.exception.DuplicateException;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.mapper.book.BookMapper;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.model.Category;
import org.project.name.online.book.store.repository.book.BookRepository;
import org.project.name.online.book.store.repository.book.BookSpecificationBuilder;
import org.project.name.online.book.store.repository.category.CategoryRepository;
import org.project.name.online.book.store.service.book.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        checkIfIsbnIsDuplicate(bookDto.getIsbn());
        List<Category> categories = getListOfCategories(bookDto.getCategoryIds());
        Book book = bookMapper.toModel(bookDto, categories);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("There is no book by id: " + id));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto updateBookById(Long id, UpdateBookRequestDto bookDto) {
        checkIfIsbnIsDuplicate(bookDto.getIsbn());
        Book bookById = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no book by id: " + id));
        List<Category> categories = getListOfCategories(bookDto.getCategoryIds());
        Book book = bookMapper.mergeEntities(bookDto, bookById, categories);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> searchBook(BookSearchParameters searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id, Pageable pageable) {
        return bookRepository.findAllByCategoryId(id, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no book by id: " + id));
        bookRepository.deleteById(id);
    }

    private void checkIfIsbnIsDuplicate(String isbn) {
        if (bookRepository.findByIsbn(isbn).isPresent()) {
            throw new DuplicateException(
                    "This isbn: " + isbn + ", is already registered in the data base");
        }
    }

    private List<Category> getListOfCategories(List<Long> bookCategories) {
        return bookCategories.stream()
                .map(categoryId -> categoryRepository.findById(categoryId).orElseThrow(
                        () -> new EntityNotFoundException(
                                "There is no category by id: " + categoryId)))
                .toList();
    }
}
