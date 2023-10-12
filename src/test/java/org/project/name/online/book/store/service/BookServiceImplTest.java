package org.project.name.online.book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.project.name.online.book.store.service.book.impl.BookServiceImpl;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    private static Category category;
    private static Book expectedBook;
    private static BookDto expectedDto;
    private static Category firstCategory;
    private static Category secondCategory;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeAll
    static void beforeAll() {
        category = new Category();
        category.setId(1L);
        category.setName("Test name");
        category.setDescription("Test name descr.");
        expectedBook = new Book();
        expectedBook.setId(1L);
        expectedBook.setTitle("Title");
        expectedBook.setAuthor("Author");
        expectedBook.setIsbn("123456789");
        expectedBook.setPrice(BigDecimal.valueOf(99.99));
        expectedBook.setDescription("Descr.");
        expectedBook.setCoverImage("Image");
        expectedBook.setCategories(Set.of(category));
        expectedDto = new BookDto();
        expectedDto.setId(1L);
        expectedDto.setTitle("Title");
        expectedDto.setAuthor("Author");
        expectedDto.setIsbn("123456789");
        expectedDto.setPrice(99.99);
        expectedDto.setDescription("Descr.");
        expectedDto.setCoverImage("Image");
        expectedDto.setCategoryIds(List.of("Test name"));
        firstCategory = new Category();
        firstCategory.setId(1L);
        firstCategory.setName("Test name");
        firstCategory.setDescription("Test name descr.");
        secondCategory = new Category();
        secondCategory.setId(2L);
        secondCategory.setName("Test name2");
        secondCategory.setDescription("Test name descr.2");
    }

    @Test
    @DisplayName("Save book with valid input bookRequestDto")
    void save_WithValidFields_ReturnsBookDto() {
        CreateBookRequestDto inputDto = createDtoRequest();
        Book book = createBookWithoutId();
        when(bookRepository.findByIsbn(inputDto.getIsbn())).thenReturn(Optional.empty());
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(bookMapper.toModel(inputDto, List.of(category))).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(expectedBook);
        when(bookMapper.toDto(expectedBook)).thenReturn(expectedDto);

        BookDto actualDto = bookService.save(inputDto);

        assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actualDto));
        verify(bookRepository, times(1)).findByIsbn(inputDto.getIsbn());
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(bookMapper, times(1)).toModel(inputDto, List.of(category));
        verify(bookMapper, times(1)).toDto(expectedBook);
        verify(bookRepository, times(1)).save(book);
        verifyNoMoreInteractions(bookRepository, categoryRepository, bookMapper);
    }

    @Test
    @DisplayName("Find a book by book id")
    void getBookById_WhichPersistInDb_ReturnsBookDto() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(expectedBook));
        when(bookMapper.toDto(expectedBook)).thenReturn(expectedDto);

        BookDto actualDto = bookService.getBookById(bookId);

        assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actualDto));
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapper, times(1)).toDto(expectedBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find all books")
    void findAll_WhichPersistInDb_ReturnsListOfBookDto() {
        Book firstBook = createFirstBookWithFirstCategory();
        BookDto firstBookDto = createFirstBookDto();
        Book secondBook = createSecondBookWithSecondCategory();
        BookDto secondBookDto = createSecondBookDto();
        List<Book> books = List.of(firstBook, secondBook);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Book> bookPage = new PageImpl<>(
                books, pageable, books.size()
        );
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(firstBook)).thenReturn(firstBookDto);
        when(bookMapper.toDto(secondBook)).thenReturn(secondBookDto);

        List<BookDto> expectedDtoList = List.of(firstBookDto, secondBookDto);
        List<BookDto> actualDtoList = bookService.findAll(pageable);

        assertEquals(expectedDtoList.size(), actualDtoList.size());
        assertTrue(EqualsBuilder.reflectionEquals(expectedDtoList.get(0), actualDtoList.get(0)));
        assertTrue(EqualsBuilder.reflectionEquals(expectedDtoList.get(1), actualDtoList.get(1)));
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(firstBook);
        verify(bookMapper, times(1)).toDto(secondBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Update a book by book id")
    void updateBookById_WhichPersistInDb_ReturnsBookDto() {
        Book expectedBook = createExpectedBook();
        Book updatedBook = createUpdatedBook();
        UpdateBookRequestDto inputDto = createUpdateDtoRequest();
        BookDto expectedBookDto = createExpectedBookDtoWithUpdates();
        Long bookId = 1L;
        when(bookRepository.findByIsbn(inputDto.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(expectedBook));
        when(categoryRepository.findById(
                secondCategory.getId())
        ).thenReturn(Optional.of(secondCategory));
        when(bookMapper.mergeEntities(
                inputDto, expectedBook, List.of(secondCategory))
        ).thenReturn(updatedBook);
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(expectedBookDto);

        BookDto actualBookDto = bookService.updateBookById(bookId, inputDto);

        assertTrue(EqualsBuilder.reflectionEquals(expectedBookDto, actualBookDto));
        verify(bookRepository, times(1)).findByIsbn(inputDto.getIsbn());
        verify(bookRepository, times(1)).findById(bookId);
        verify(categoryRepository, times(1)).findById(secondCategory.getId());
        verify(bookMapper, times(1)).mergeEntities(
                inputDto, expectedBook, List.of(secondCategory)
        );
        verify(bookMapper, times(1)).toDto(updatedBook);
        verify(bookRepository, times(1)).save(updatedBook);
        verifyNoMoreInteractions(bookRepository,categoryRepository, bookMapper);
    }

    @Test
    @DisplayName("Find books by specifications")
    void searchBook_ByValidSearchParameters_ReturnsListOfBookDto() {
        Book book = createBookWithId();
        BookDto expectedBookDto = createExpectedBookDto();
        BookSearchParameters bookSearchParameters = new BookSearchParameters(
                new String[]{"Author"}, new String[]{"99", "100"}
        );
        Specification<Book> specification = mock(Specification.class);
        List<Book> books = List.of(book);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Book> bookPage = new PageImpl<>(
                books, pageable, books.size()
        );
        when(bookSpecificationBuilder.build(bookSearchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        List<BookDto> expectedDtoList = List.of(expectedBookDto);
        List<BookDto> actualDtoList = bookService.searchBook(bookSearchParameters, pageable);

        assertEquals(expectedDtoList.size(), actualDtoList.size());
        assertTrue(EqualsBuilder.reflectionEquals(expectedDtoList.get(0), actualDtoList.get(0)));
        verify(bookSpecificationBuilder, times(1)).build(bookSearchParameters);
        verify(bookRepository, times(1)).findAll(specification, pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookSpecificationBuilder, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find all books by category id")
    void findAllByCategoryId_WhichPersistInDb_ReturnsListOfBookDto() {
        Book firstBook = createFirstBook();
        BookDtoWithoutCategoryIds firstBookDto = createFirstDtoWithoutCategoryIds();
        Book secondBook = createSecondBook();
        BookDtoWithoutCategoryIds secondBookDto = createSecondDtoWithoutCategoryIds();
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(firstBook, secondBook);
        Long categoryId = 1L;
        when(bookRepository.findAllByCategoryId(categoryId, pageable)).thenReturn(books);
        when(bookMapper.toDtoWithoutCategories(firstBook)).thenReturn(firstBookDto);
        when(bookMapper.toDtoWithoutCategories(secondBook)).thenReturn(secondBookDto);

        List<BookDtoWithoutCategoryIds> expectedDtoList = List.of(firstBookDto, secondBookDto);
        List<BookDtoWithoutCategoryIds> actualDtoList
                = bookService.findAllByCategoryId(categoryId, pageable);

        assertEquals(expectedDtoList, actualDtoList);
        assertTrue(EqualsBuilder.reflectionEquals(expectedDtoList.get(0), actualDtoList.get(0)));
        assertTrue(EqualsBuilder.reflectionEquals(expectedDtoList.get(1), actualDtoList.get(1)));
        verify(bookRepository, times(1)).findAllByCategoryId(categoryId, pageable);
        verify(bookMapper, times(1)).toDtoWithoutCategories(firstBook);
        verify(bookMapper, times(1)).toDtoWithoutCategories(secondBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Delete a book by book id")
    void deleteById_WhichPersistInDB_ReturnsVoid() {
        Book book = new Book();
        book.setId(1L);
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        bookService.deleteById(bookId);

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("save. Throw EntityNotFoundException since the category id doesn't exist")
    void save_WithNotExistentCategory_ThrowsEntityNotFoundException() {
        Long nonExistentCategoryId = 100L;
        CreateBookRequestDto inputDto = new CreateBookRequestDto();
        inputDto.setCategoryIds(List.of(nonExistentCategoryId));
        when(categoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.save(inputDto)
        );
        String expected = "There is no category by id: " + nonExistentCategoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(nonExistentCategoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("save. Throw DuplicateException since input isbn field is already in DB")
    void save_WithDuplicateIsbn_ThrowsDuplicateException() {
        String duplicateIsbn = "duplicate isbn";
        CreateBookRequestDto inputDto = new CreateBookRequestDto();
        inputDto.setIsbn(duplicateIsbn);
        Book expectedBook = new Book();
        expectedBook.setIsbn(duplicateIsbn);
        when(bookRepository.findByIsbn(inputDto.getIsbn())).thenReturn(Optional.of(expectedBook));

        DuplicateException exception = assertThrows(
                DuplicateException.class,
                () -> bookService.save(inputDto)
        );
        String expected
                = "This isbn: " + duplicateIsbn + ", is already registered in the data base";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findByIsbn(inputDto.getIsbn());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("getBookById. Throw EntityNotFoundException since the book doesn't exist")
    void getBookById_WithNotExistentBook_ThrowsEntityNotFoundException() {
        Long nonExistentBookId = 100L;
        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getBookById(nonExistentBookId)
        );
        String expected = "There is no book by id: " + nonExistentBookId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(nonExistentBookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("findAll. Return empty list since there is no any book in DB")
    void findAll_WhichNonPersistInDb_ReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of();
        PageImpl<Book> bookPage = new PageImpl<>(
                books, pageable, 0
        );
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        List<BookDto> actualDtoList = bookService.findAll(pageable);

        assertEquals(0, actualDtoList.size());
        verify(bookRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("updateBookById. Throw DuplicateException since input isbn field is already in DB")
    void updateBookById_WithDuplicateIsbn_ThrowsDuplicateException() {
        String duplicateIsbn = "duplicate isbn";
        UpdateBookRequestDto inputDto = new UpdateBookRequestDto();
        inputDto.setIsbn(duplicateIsbn);
        Book book = new Book();
        book.setIsbn(duplicateIsbn);
        when(bookRepository.findByIsbn(duplicateIsbn)).thenReturn(Optional.of(book));

        Long bookId = 1L;
        DuplicateException exception = assertThrows(
                DuplicateException.class,
                () -> bookService.updateBookById(bookId, inputDto)
        );
        String expected
                = "This isbn: " + duplicateIsbn + ", is already registered in the data base";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findByIsbn(duplicateIsbn);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName(
            "updateBookById. Throw EntityNotFoundException since the category id doesn't exist"
    )
    void updateBookById_WithNonExistentCategory_ThrowsEntityNotFoundException() {
        Long bookId = 1L;
        Long nonExistentCategoryId = 100L;
        UpdateBookRequestDto inputDto = new UpdateBookRequestDto();
        inputDto.setCategoryIds(List.of(nonExistentCategoryId));
        inputDto.setIsbn("123456789");
        Book book = new Book();
        book.setId(bookId);
        when(bookRepository.findByIsbn(inputDto.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.updateBookById(bookId, inputDto)
        );
        String expected = "There is no category by id: " + nonExistentCategoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(bookId);
        verify(categoryRepository, times(1)).findById(nonExistentCategoryId);
        verifyNoMoreInteractions(bookRepository, categoryRepository);
    }

    @Test
    @DisplayName("updateBookById. Throw EntityNotFoundException since the book doesn't exist")
    void updateBookById_WithNonExistentBook_ThrowsEntityNotFoundException() {
        Long nonExistentBookId = 100L;
        UpdateBookRequestDto inputDto = new UpdateBookRequestDto();
        inputDto.setIsbn("123456789");
        when(bookRepository.findByIsbn(inputDto.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.updateBookById(nonExistentBookId, inputDto)
        );
        String expected = "There is no book by id: " + nonExistentBookId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findByIsbn(inputDto.getIsbn());
        verify(bookRepository, times(1)).findById(nonExistentBookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName(
            "searchBook. "
                    + "Return empty list since there is no any book with this specification in DB"
    )
    void searchBook_WithEmptySearchList_ReturnsEmptyList() {
        Specification<Book> specification = mock(Specification.class);
        BookSearchParameters bookSearchParameters = new BookSearchParameters(
                new String[]{}, new String[]{});
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of();
        PageImpl<Book> bookPage = new PageImpl<>(
                books, pageable, 0
        );
        when(bookSpecificationBuilder.build(bookSearchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(bookPage);

        List<BookDto> actualDtoList = bookService.searchBook(bookSearchParameters, pageable);

        assertEquals(0, actualDtoList.size());
        verify(bookSpecificationBuilder, times(1)).build(bookSearchParameters);
        verify(bookRepository, times(1)).findAll(specification, pageable);
        verifyNoMoreInteractions(bookSpecificationBuilder, bookRepository);
    }

    @Test
    @DisplayName("findAllByCategoryId. Returns empty list since category id doesn't exist")
    void findAllByCategoryId_ByNonExistentCategoryId_ReturnsEmptyList() {
        Long nonExistentCategoryId = 100L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of();
        when(bookRepository.findAllByCategoryId(nonExistentCategoryId, pageable)).thenReturn(books);

        List<BookDtoWithoutCategoryIds> actualDtoList
                = bookService.findAllByCategoryId(nonExistentCategoryId, pageable);

        assertEquals(0, actualDtoList.size());
        verify(bookRepository, times(1)).findAllByCategoryId(nonExistentCategoryId, pageable);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("deleteById. Throw EntityNotFoundException since the book doesn't exist")
    void deleteById_ByNonExistentBookId_ThrowsEntityNotFoundException() {
        Long nonExistentBookId = 100L;
        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.deleteById(nonExistentBookId)
        );
        String expected = "There is no book by id: " + nonExistentBookId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(nonExistentBookId);
        verifyNoMoreInteractions(bookRepository);
    }

    private CreateBookRequestDto createDtoRequest() {
        CreateBookRequestDto inputDto = new CreateBookRequestDto();
        inputDto.setTitle("Title");
        inputDto.setAuthor("Author");
        inputDto.setIsbn("123456789");
        inputDto.setPrice(99.99);
        inputDto.setDescription("Descr.");
        inputDto.setCoverImage("Image");
        inputDto.setCategoryIds(List.of(1L));
        return inputDto;
    }

    private Book createBookWithoutId() {
        Book book = new Book();
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setIsbn("123456789");
        book.setPrice(BigDecimal.valueOf(99.99));
        book.setDescription("Descr.");
        book.setCoverImage("Image");
        book.setCategories(Set.of(category));
        return book;
    }

    private Book createFirstBookWithFirstCategory() {
        Book firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setTitle("Title");
        firstBook.setAuthor("Author");
        firstBook.setIsbn("123456789");
        firstBook.setPrice(BigDecimal.valueOf(99.99));
        firstBook.setDescription("Descr.");
        firstBook.setCoverImage("Image");
        firstBook.setCategories(Set.of(firstCategory));
        return firstBook;
    }

    private Book createSecondBookWithSecondCategory() {
        Book secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setTitle("Title2");
        secondBook.setAuthor("Author2");
        secondBook.setIsbn("123456789(2)");
        secondBook.setPrice(BigDecimal.valueOf(98.99));
        secondBook.setDescription("Descr.2");
        secondBook.setCoverImage("Image2");
        secondBook.setCategories(Set.of(secondCategory));
        return secondBook;
    }

    private BookDto createFirstBookDto() {
        BookDto firstBookDto = new BookDto();
        firstBookDto.setId(1L);
        firstBookDto.setTitle("Title");
        firstBookDto.setAuthor("Author");
        firstBookDto.setIsbn("123456789");
        firstBookDto.setPrice(99.99);
        firstBookDto.setDescription("Descr.");
        firstBookDto.setCoverImage("Image");
        firstBookDto.setCategoryIds(List.of("Test name"));
        return firstBookDto;
    }

    private BookDto createSecondBookDto() {
        BookDto secondBookDto = new BookDto();
        secondBookDto.setId(2L);
        secondBookDto.setTitle("Title2");
        secondBookDto.setAuthor("Author2");
        secondBookDto.setIsbn("123456789(2)");
        secondBookDto.setPrice(98.99);
        secondBookDto.setDescription("Descr.2");
        secondBookDto.setCoverImage("Image2");
        secondBookDto.setCategoryIds(List.of("Test name2"));
        return secondBookDto;
    }

    private Book createExpectedBook() {
        Book expectedBook = new Book();
        expectedBook.setId(1L);
        expectedBook.setTitle("Title");
        expectedBook.setAuthor("Author");
        expectedBook.setIsbn("123456789");
        expectedBook.setPrice(BigDecimal.valueOf(99.99));
        expectedBook.setDescription("Descr.");
        expectedBook.setCoverImage("Image");
        expectedBook.setCategories(Set.of(firstCategory));
        return expectedBook;
    }

    private Book createUpdatedBook() {
        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Update title");
        updatedBook.setAuthor("Update author");
        updatedBook.setIsbn("Update isbn");
        updatedBook.setPrice(BigDecimal.valueOf(34.54));
        updatedBook.setCoverImage("Update image");
        updatedBook.setCategories(Set.of(secondCategory));
        return updatedBook;
    }

    private UpdateBookRequestDto createUpdateDtoRequest() {
        UpdateBookRequestDto inputDto = new UpdateBookRequestDto();
        inputDto.setTitle("Update title");
        inputDto.setAuthor("Update author");
        inputDto.setIsbn("Update isbn");
        inputDto.setPrice(34.54);
        inputDto.setDescription("Update descr.");
        inputDto.setCoverImage("Update image");
        inputDto.setCategoryIds(List.of(2L));
        return inputDto;
    }

    private BookDto createExpectedBookDtoWithUpdates() {
        BookDto expectedBookDto = new BookDto();
        expectedBookDto.setId(1L);
        expectedBookDto.setTitle("Update title");
        expectedBookDto.setAuthor("Update author");
        expectedBookDto.setIsbn("Update isbn");
        expectedBookDto.setPrice(34.54);
        expectedBookDto.setDescription("Update descr.");
        expectedBookDto.setCoverImage("Update image");
        expectedBookDto.setCategoryIds(List.of("Test name2"));
        return expectedBookDto;
    }

    private Book createBookWithId() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setIsbn("123456789");
        book.setPrice(BigDecimal.valueOf(99.99));
        book.setDescription("Descr.");
        book.setCoverImage("Image");
        book.setCategories(Set.of(category));
        return book;
    }

    private BookDto createExpectedBookDto() {
        BookDto expectedBookDto = new BookDto();
        expectedBookDto.setId(1L);
        expectedBookDto.setTitle("Title");
        expectedBookDto.setAuthor("Author");
        expectedBookDto.setIsbn("123456789");
        expectedBookDto.setPrice(99.99);
        expectedBookDto.setDescription("Descr.");
        expectedBookDto.setCoverImage("Image");
        expectedBookDto.setCategoryIds(List.of("Test name"));
        return expectedBookDto;
    }

    private Book createFirstBook() {
        Book firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setTitle("Title");
        firstBook.setAuthor("Author");
        firstBook.setIsbn("123456789");
        firstBook.setPrice(BigDecimal.valueOf(99.99));
        firstBook.setDescription("Descr.");
        firstBook.setCoverImage("Image");
        firstBook.setCategories(Set.of(category));
        return firstBook;
    }

    private BookDtoWithoutCategoryIds createFirstDtoWithoutCategoryIds() {
        BookDtoWithoutCategoryIds firstBookDto = new BookDtoWithoutCategoryIds();
        firstBookDto.setId(1L);
        firstBookDto.setTitle("Title");
        firstBookDto.setAuthor("Author");
        firstBookDto.setIsbn("123456789");
        firstBookDto.setPrice(99.99);
        firstBookDto.setDescription("Descr.");
        firstBookDto.setCoverImage("Image");
        return firstBookDto;
    }

    private Book createSecondBook() {
        Book secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setTitle("Title2");
        secondBook.setAuthor("Author2");
        secondBook.setIsbn("123456789(2)");
        secondBook.setPrice(BigDecimal.valueOf(98.99));
        secondBook.setDescription("Descr.2");
        secondBook.setCoverImage("Image2");
        secondBook.setCategories(Set.of(category));
        return secondBook;
    }

    private BookDtoWithoutCategoryIds createSecondDtoWithoutCategoryIds() {
        BookDtoWithoutCategoryIds secondBookDto = new BookDtoWithoutCategoryIds();
        secondBookDto.setId(2L);
        secondBookDto.setTitle("Title2");
        secondBookDto.setAuthor("Author2");
        secondBookDto.setIsbn("123456789(2)");
        secondBookDto.setPrice(98.99);
        secondBookDto.setDescription("Descr.2");
        secondBookDto.setCoverImage("Image2");
        return secondBookDto;
    }
}
