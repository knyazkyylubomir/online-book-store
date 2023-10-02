package org.project.name.online.book.store.repository.cart.item;

import java.util.List;
import org.project.name.online.book.store.model.CartItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByShoppingCartId(Long shoppingCartId);

    List<CartItem> findAllByShoppingCartId(Long shoppingCartId, Pageable pageable);
}
