package org.project.name.online.book.store.service.shopping.cart;

import org.project.name.online.book.store.dto.cart.item.CreateCartItemRequestDto;
import org.project.name.online.book.store.dto.cart.item.UpdateCartItemRequestDto;
import org.project.name.online.book.store.dto.shopping.cart.ShoppingCartDto;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {
    void save(String email, CreateCartItemRequestDto cartItemDto);

    ShoppingCartDto getCartByUserId(String email, Pageable pageable);

    void update(String email, Long cartItemId, UpdateCartItemRequestDto cartItemDto);

    void delete(String email, Long cartItemId);
}
