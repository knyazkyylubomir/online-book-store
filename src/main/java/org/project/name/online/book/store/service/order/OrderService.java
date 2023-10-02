package org.project.name.online.book.store.service.order;

import java.util.List;
import org.project.name.online.book.store.dto.order.CreateOrderRequestDto;
import org.project.name.online.book.store.dto.order.OrderDto;
import org.project.name.online.book.store.dto.order.OrderDtoWithStatus;
import org.project.name.online.book.store.dto.order.UpdateOrderRequestDto;
import org.project.name.online.book.store.dto.order.item.OrderItemDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    void save(String email, CreateOrderRequestDto orderDto);

    List<OrderDto> getAll(String email, Pageable pageable);

    OrderDtoWithStatus update(Long id, UpdateOrderRequestDto orderDto);

    List<OrderItemDto> getAllByOrderId(String email, Long orderId, Pageable pageable);

    OrderItemDto getById(String email, Long orderId, Long itemId);
}
