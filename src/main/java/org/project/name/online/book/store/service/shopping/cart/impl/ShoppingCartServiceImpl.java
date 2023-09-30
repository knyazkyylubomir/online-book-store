package org.project.name.online.book.store.service.shopping.cart.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.dto.cart.item.CreateCartItemRequestDto;
import org.project.name.online.book.store.dto.cart.item.UpdateCartItemRequestDto;
import org.project.name.online.book.store.dto.shopping.cart.ShoppingCartDto;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.mapper.cart.item.CartItemMapper;
import org.project.name.online.book.store.mapper.shopping.cart.ShoppingCartMapper;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.model.CartItem;
import org.project.name.online.book.store.model.ShoppingCart;
import org.project.name.online.book.store.model.User;
import org.project.name.online.book.store.repository.book.BookRepository;
import org.project.name.online.book.store.repository.cart.item.CartItemRepository;
import org.project.name.online.book.store.repository.shopping.cart.ShoppingCartRepository;
import org.project.name.online.book.store.repository.user.UserRepository;
import org.project.name.online.book.store.service.shopping.cart.ShoppingCartService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public void save(String email, CreateCartItemRequestDto cartItemDto) {
        ShoppingCart shoppingCart = getShoppingCartByEmail(email);
        List<CartItem> cartItems = cartItemRepository.findAllByShoppingCartId(shoppingCart.getId());
        for (CartItem cartItem : cartItems) {
            if (cartItem.getBook().getId().equals(cartItemDto.getBookId())) {
                cartItem.setQuantity(cartItem.getQuantity() + cartItemDto.getQuantity());
                cartItemRepository.save(cartItem);
                return;
            }
        }
        Book book = bookRepository.findById(cartItemDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "There is no book by id: " + cartItemDto.getBookId()));
        CartItem cartItem = cartItemMapper.toEntity(shoppingCart, book, cartItemDto);
        cartItemRepository.save(cartItem);
    }

    @Override
    public ShoppingCartDto getCartByUserId(String email, Pageable pageable) {
        ShoppingCart shoppingCart = getShoppingCartByEmail(email);
        List<CartItem> cartItems
                = cartItemRepository.findAllByShoppingCartId(shoppingCart.getId(), pageable);
        return shoppingCartMapper.toDto(shoppingCart, cartItemMapper.toDtoList(cartItems));
    }

    @Override
    public void update(String email, Long cartItemId, UpdateCartItemRequestDto cartItemDto) {
        ShoppingCart shoppingCart = getShoppingCartByEmail(email);
        List<CartItem> cartItems = cartItemRepository.findAllByShoppingCartId(shoppingCart.getId());
        cartItems.stream()
                .filter(cartItem -> cartItem.getId().equals(cartItemId))
                .findFirst()
                .map(cartItem -> {
                    cartItem.setQuantity(cartItemDto.getQuantity());
                    return cartItemRepository.save(cartItem);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        "There is no cart-item by id: " + cartItemId));
    }

    @Override
    public void delete(String email, Long cartItemId) {
        cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("There is no cart-item by id: " + cartItemId));
        ShoppingCart shoppingCart = getShoppingCartByEmail(email);
        List<CartItem> cartItems = cartItemRepository.findAllByShoppingCartId(shoppingCart.getId());
        cartItems.stream()
                .filter(cartItem -> cartItem.getId().equals(cartItemId))
                .forEach(cartItemRepository::delete);
    }

    private ShoppingCart getShoppingCartByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("There is no user by email: " + email));
        return shoppingCartRepository.findByUserId(user.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "There is no shopping cart by user id: " + user.getId()));
    }
}
