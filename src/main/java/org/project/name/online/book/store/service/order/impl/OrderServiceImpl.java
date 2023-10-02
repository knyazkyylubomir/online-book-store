package org.project.name.online.book.store.service.order.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.dto.order.CreateOrderRequestDto;
import org.project.name.online.book.store.dto.order.OrderDto;
import org.project.name.online.book.store.dto.order.OrderDtoWithStatus;
import org.project.name.online.book.store.dto.order.UpdateOrderRequestDto;
import org.project.name.online.book.store.dto.order.item.OrderItemDto;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.exception.PriceException;
import org.project.name.online.book.store.exception.StatusException;
import org.project.name.online.book.store.mapper.order.OrderMapper;
import org.project.name.online.book.store.mapper.order.item.OrderItemMapper;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.model.CartItem;
import org.project.name.online.book.store.model.Order;
import org.project.name.online.book.store.model.OrderItem;
import org.project.name.online.book.store.model.ShoppingCart;
import org.project.name.online.book.store.model.User;
import org.project.name.online.book.store.repository.book.BookRepository;
import org.project.name.online.book.store.repository.cart.item.CartItemRepository;
import org.project.name.online.book.store.repository.order.OrderRepository;
import org.project.name.online.book.store.repository.order.item.OrderItemRepository;
import org.project.name.online.book.store.repository.shopping.cart.ShoppingCartRepository;
import org.project.name.online.book.store.repository.user.UserRepository;
import org.project.name.online.book.store.service.order.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public void save(String email, CreateOrderRequestDto orderDto) {
        User user = getUserByEmail(email);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "There is no shopping cart by user id: " + user.getId()));
        List<CartItem> cartItems = cartItemRepository.findAllByShoppingCartId(shoppingCart.getId());
        Order order = orderMapper.toEntity(shoppingCart, Order.Status.PENDING, new BigDecimal(0),
                orderDto, LocalDateTime.now());
        orderRepository.save(order);
        saveOrderItemsToOrder(cartItems, order);
        if (order.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PriceException("The order cannot be processed, "
                    + "since your shopping cart is empty! Add a few books there");
        }
        orderRepository.save(order);
    }

    @Override
    public List<OrderDto> getAll(String email, Pageable pageable) {
        User user = getUserByEmail(email);
        List<Order> orders = orderRepository.findAllByUserId(user.getId(), pageable);
        return orders.stream()
                .map(order -> {
                    List<OrderItem> orderItems
                            = orderItemRepository.findAllByOrderId(order.getId());
                    List<OrderItemDto> orderItemsDto = orderItems.stream()
                            .map(orderItemMapper::toDto)
                            .toList();
                    return orderMapper.toDto(order, orderItemsDto);
                }).toList();
    }

    @Override
    public OrderDtoWithStatus update(Long id, UpdateOrderRequestDto orderDto) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no order by id: " + id));
        Arrays.stream(Order.Status.values())
                .filter(status -> status.name().equals(orderDto.getStatus()))
                .map(status -> orderMapper.mergeEntities(order, orderDto))
                .findFirst()
                .orElseThrow(() -> new StatusException(
                        "The status is not correct! Make sure the status is correctly written"));
        Order mergedOrder = orderMapper.mergeEntities(order, orderDto);
        return orderMapper.toDtoWithStatus(orderRepository.save(mergedOrder));
    }

    @Override
    public List<OrderItemDto> getAllByOrderId(String email, Long orderId, Pageable pageable) {
        User user = getUserByEmail(email);
        List<Order> orders = orderRepository.findAllByUserId(user.getId(), pageable);
        Order orderById = getOrderById(orders, orderId);
        List<OrderItem> orderItems
                = orderItemRepository.findAllByOrderId(orderById.getId(), pageable);
        return orderItemMapper.toDtoList(orderItems);
    }

    @Override
    public OrderItemDto getById(String email, Long orderId, Long itemId) {
        User user = getUserByEmail(email);
        List<Order> orders = orderRepository.findAllByUserId(user.getId());
        Order orderById = getOrderById(orders, orderId);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderById.getId());
        OrderItem orderItemById = orderItems.stream()
                .filter(orderItem -> orderItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "There is no order-item by id: " + itemId));
        return orderItemMapper.toDto(orderItemById);
    }

    private void saveOrderItemsToOrder(List<CartItem> cartItems, Order order) {
        cartItems.forEach(cartItem -> {
            BigDecimal total = order.getTotal();
            Book book = bookRepository.findById(cartItem.getBook().getId()).orElseThrow(
                    () -> new EntityNotFoundException(
                            "There is no book by id: " + cartItem.getBook().getId()));
            OrderItem orderItem = orderItemMapper.toEntity(order, cartItem);
            orderItem.setPrice(book.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            order.setTotal(
                    total.add(book.getPrice().multiply(new BigDecimal(cartItem.getQuantity()))));
            orderItemRepository.save(orderItem);
            cartItemRepository.delete(cartItem);
        });
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("There is no user by email: " + email));
    }

    private Order getOrderById(List<Order> orders, Long orderId) {
        return orders.stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("There is no order by id: " + orderId));
    }
}
