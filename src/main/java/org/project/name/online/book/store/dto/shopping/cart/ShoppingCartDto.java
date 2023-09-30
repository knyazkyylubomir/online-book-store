package org.project.name.online.book.store.dto.shopping.cart;

import java.util.List;
import lombok.Data;
import org.project.name.online.book.store.dto.cart.item.CartItemDto;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> cartItems;
}
