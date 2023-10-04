package org.project.name.online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.dto.order.CreateOrderRequestDto;
import org.project.name.online.book.store.dto.order.OrderDto;
import org.project.name.online.book.store.dto.order.OrderDtoWithStatus;
import org.project.name.online.book.store.dto.order.UpdateOrderRequestDto;
import org.project.name.online.book.store.dto.order.item.OrderItemDto;
import org.project.name.online.book.store.service.order.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order API", description = "Endpoints for managing orders and their history")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Place an order",
            description = "This endpoint places an order by user defined shipping address")
    public void placeOrder(
            Authentication authentication,
            @RequestBody @Valid CreateOrderRequestDto orderDto
    ) {
        String email = authentication.getName();
        orderService.save(email, orderDto);
    }

    @GetMapping
    @Operation(summary = "Receive order history",
            description = "This endpoint receives a user's order history")
    public List<OrderDto> getAllOrders(Authentication authentication, Pageable pageable) {
        String email = authentication.getName();
        return orderService.getAll(email, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update order status",
            description = "This endpoint updates an order status by order id")
    public OrderDtoWithStatus updateOrderStatus(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid UpdateOrderRequestDto orderDto
    ) {
        return orderService.update(id, orderDto);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Receive all order-items",
            description = "This endpoint receives all order-items by an order id")
    public List<OrderItemDto> getAllOrderItems(
            Authentication authentication,
            @PathVariable @Min(1) Long orderId,
            Pageable pageable
    ) {
        String email = authentication.getName();
        return orderService.getAllByOrderId(email, orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Receive an order-item by the order",
            description = "This endpoint receives an order-item by an order id")
    public OrderItemDto getItemByIdWithinOrder(
            Authentication authentication,
            @PathVariable @Min(1) Long orderId,
            @PathVariable @Min(1) Long itemId
    ) {
        String email = authentication.getName();
        return orderService.getById(email, orderId, itemId);
    }
}
