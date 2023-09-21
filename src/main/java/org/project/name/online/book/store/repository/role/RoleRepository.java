package org.project.name.online.book.store.repository.role;

import java.util.Optional;
import org.project.name.online.book.store.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(Role.RoleName roleName);
}
