package org.project.name.online.book.store.repository.user;

import java.util.Optional;
import org.project.name.online.book.store.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(value = "User.roles")
    Optional<User> findByEmail(String email);
}
