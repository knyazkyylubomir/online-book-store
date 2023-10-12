package org.project.name.online.book.store.repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.model.Category;
import org.project.name.online.book.store.repository.book.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryIntegrationTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find a book by existent isbn")
    @Sql(scripts = {
            "classpath:database/books/repository/findByIsbn/"
                    + "add-book-to-books-table.sql",
            "classpath:database/books/repository/findByIsbn/"
                    + "add-book_category-pairs-to-books-categories.sql",
            "classpath:database/books/repository/findByIsbn/"
                    + "add-category-to-categories-table.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/repository/findByIsbn/"
            + "remove-everything-after-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIsbn_ExistentIsbn_ReturnsBook() {
        Book expected = createExpectedBook();

        Book actual = bookRepository.findByIsbn("123456789").orElseThrow(
                () -> new EntityNotFoundException("Error occurred!"));

        Assertions.assertNotNull(actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Find books by category id")
    @Sql(scripts = {
            "classpath:database/books/repository/findAllByCategoryId/"
                    + "add-books-with-category-to-books-table.sql",
            "classpath:database/books/repository/findAllByCategoryId/"
                    + "add-book_category-pairs-to-books_categories.sql",
            "classpath:database/books/repository/findAllByCategoryId/"
                    + "add-categories-to-categories-table.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/repository/findAllByCategoryId/"
            + "remove-everything-after-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ExistentCategoryId_ReturnsBooks() {
        List<Book> expected = createExpectedListOfBook();
        Pageable pageable = PageRequest.of(0, 10);

        List<Book> actual = bookRepository.findAllByCategoryId(1L, pageable);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.get(0));
        Assertions.assertNotNull(actual.get(1));
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0)));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected.get(1), actual.get(1)));
    }

    @Test
    @DisplayName("Find book by category id")
    @Sql(scripts = {
            "classpath:database/books/repository/findAllByCategoryId/"
                    + "add-books-with-category-to-books-table.sql",
            "classpath:database/books/repository/findAllByCategoryId/"
                    + "add-book_category-pairs-to-books_categories.sql",
            "classpath:database/books/repository/findAllByCategoryId/"
                    + "add-categories-to-categories-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/repository/findAllByCategoryId/"
            + "remove-everything-after-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ExistentCategoryId_ReturnsBook() {
        List<Book> expected = createBookListOfOneBook();
        Pageable pageable = PageRequest.of(0, 10);

        List<Book> actual = bookRepository.findAllByCategoryId(2L, pageable);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.get(0));
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0)));
    }

    @Test
    @DisplayName("Returns optional empty since book with given isbn doesn't exist")
    void findByIsbn_NonExistentIsbn_ReturnsOptionalEmpty() {
        Optional<Object> expected = Optional.empty();

        String isbn = "123456789";
        Optional<Book> actual = bookRepository.findByIsbn(isbn);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Returns optional empty since null as input parameter")
    void findByIsbn_NullAsInputParameter_ReturnsOptionalEmpty() {
        Optional<Object> expected = Optional.empty();

        Optional<Book> actual = bookRepository.findByIsbn(null);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getBookById_() {
    }

    @Test
    @DisplayName("Returns empty list since there is no the category id")
    void findAllByCategoryId_NonExistentCategoryId_ReturnsEmptyList() {
        Long categoryId = 5L;
        List<Object> expected = Collections.emptyList();
        Pageable pageable = PageRequest.of(0, 10);

        List<Book> actual = bookRepository.findAllByCategoryId(categoryId, pageable);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Returns empty list since null as input parameter")
    void findAllByCategoryId_NullAsInputParameter_ReturnsEmptyList() {
        List<Object> expected = Collections.emptyList();
        Pageable pageable = PageRequest.of(0, 10);

        List<Book> actual = bookRepository.findAllByCategoryId(null, pageable);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    private Book createExpectedBook() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test name");
        category.setDescription("Test name descr.");
        Book expected = new Book();
        expected.setId(1L);
        expected.setTitle("Title");
        expected.setAuthor("Author");
        expected.setIsbn("123456789");
        expected.setDescription("Descr.");
        expected.setCoverImage("Image");
        expected.setPrice(BigDecimal.valueOf(99.99));
        expected.setCategories(Set.of(category));
        return expected;
    }

    private List<Book> createExpectedListOfBook() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test name");
        category.setDescription("Test name descr.");
        Book first = new Book();
        first.setId(1L);
        first.setTitle("Title");
        first.setAuthor("Author");
        first.setIsbn("123456789");
        first.setPrice(BigDecimal.valueOf(99.99));
        first.setDescription("Descr.");
        first.setCoverImage("Image");
        first.setCategories(Set.of(category));
        Book second = new Book();
        second.setId(2L);
        second.setTitle("Title2");
        second.setAuthor("Author2");
        second.setIsbn("123456789(2)");
        second.setPrice(BigDecimal.valueOf(98.99));
        second.setDescription("Descr.2");
        second.setCoverImage("Image2");
        second.setCategories(Set.of(category));
        return List.of(first, second);
    }

    private List<Book> createBookListOfOneBook() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Test name2");
        category.setDescription("Test name descr.2");
        Book book = new Book();
        book.setId(3L);
        book.setTitle("Title3");
        book.setAuthor("Author3");
        book.setIsbn("123456789(3)");
        book.setPrice(BigDecimal.valueOf(97.99));
        book.setDescription("Descr.3");
        book.setCoverImage("Image3");
        book.setCategories(Set.of(category));
        return List.of(book);
    }
}
