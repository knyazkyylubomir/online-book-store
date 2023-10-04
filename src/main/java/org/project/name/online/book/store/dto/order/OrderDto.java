package org.project.name.online.book.store.dto.order;

import java.util.List;
import lombok.Data;
import org.project.name.online.book.store.dto.order.item.OrderItemDto;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private List<OrderItemDto> orderItems;
    private String orderDate;
    private Double total;
    private String status;
}
