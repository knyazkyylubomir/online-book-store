package org.project.name.online.book.store.repository.shopping.cart;

import java.util.Optional;
import org.project.name.online.book.store.model.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @EntityGraph(value = "ShoppingCart.cartItems")
    Optional<ShoppingCart> findByUserId(Long userId);
}
