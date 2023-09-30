package org.project.name.online.book.store.repository.category;

import java.util.Optional;
import org.project.name.online.book.store.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
