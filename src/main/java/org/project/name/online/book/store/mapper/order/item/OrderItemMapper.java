package org.project.name.online.book.store.mapper.order.item;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.name.online.book.store.config.MapperConfig;
import org.project.name.online.book.store.dto.order.item.OrderItemDto;
import org.project.name.online.book.store.model.CartItem;
import org.project.name.online.book.store.model.Order;
import org.project.name.online.book.store.model.OrderItem;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "deleted")
    @Mapping(ignore = true, target = "price")
    OrderItem toEntity(Order order, CartItem cartItem);

    @Mapping(source = "orderItem.book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);

    List<OrderItemDto> toDtoList(List<OrderItem> orderItems);
}
