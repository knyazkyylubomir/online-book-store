package org.project.name.online.book.store.mapper.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.project.name.online.book.store.config.MapperConfig;
import org.project.name.online.book.store.dto.order.CreateOrderRequestDto;
import org.project.name.online.book.store.dto.order.OrderDto;
import org.project.name.online.book.store.dto.order.OrderDtoWithStatus;
import org.project.name.online.book.store.dto.order.UpdateOrderRequestDto;
import org.project.name.online.book.store.dto.order.item.OrderItemDto;
import org.project.name.online.book.store.model.Order;
import org.project.name.online.book.store.model.ShoppingCart;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "dateTime", target = "orderDate")
    @Mapping(ignore = true, target = "deleted")
    Order toEntity(
            ShoppingCart shoppingCart,
            Order.Status status,
            BigDecimal total,
            CreateOrderRequestDto orderDto,
            LocalDateTime dateTime);

    @Mapping(source = "order.user.id", target = "userId")
    @Mapping(source = "orderItemsDto", target = "orderItems")
    OrderDto toDto(Order order, List<OrderItemDto> orderItemsDto);

    Order mergeEntities(@MappingTarget Order order, UpdateOrderRequestDto orderDto);

    OrderDtoWithStatus toDtoWithStatus(Order order);
}
