package org.project.name.online.book.store.repository.order.item;

import java.util.List;
import org.project.name.online.book.store.model.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(Long orderId);

    List<OrderItem> findAllByOrderId(Long orderId, Pageable pageable);
}
