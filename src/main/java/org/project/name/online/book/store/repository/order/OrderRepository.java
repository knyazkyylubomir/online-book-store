package org.project.name.online.book.store.repository.order;

import java.util.List;
import org.project.name.online.book.store.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId);

    List<Order> findAllByUserId(Long userId, Pageable pageable);
}
