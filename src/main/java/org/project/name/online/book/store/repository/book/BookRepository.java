package org.project.name.online.book.store.repository.book;

import java.util.List;
import java.util.Optional;
import org.project.name.online.book.store.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @EntityGraph(value = "Book.categories")
    @Override
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(value = "Book.categories")
    @Override
    Page<Book> findAll(Specification<Book> spec, Pageable pageable);

    @EntityGraph(value = "Book.categories")
    @Override
    Optional<Book> findById(Long id);

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories c WHERE c.id = :categoryId")
    List<Book> findAllByCategoryId(Long categoryId, Pageable pageable);
}
