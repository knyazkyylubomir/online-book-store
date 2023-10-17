package org.project.name.online.book.store.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.model.CartItem;
import org.project.name.online.book.store.repository.cart.item.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartItemRepositoryIntegrationTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Find all cart-items")
    @Sql(scripts = {
            "classpath:database/cart_items/repository/findAllByShoppingCartId/"
                    + "add-cart-item-to-cart_items-table.sql",
            "classpath:database/cart_items/repository/findAllByShoppingCartId/"
                    + "add-book-to-books-table.sql",
            "classpath:database/cart_items/repository/findAllByShoppingCartId/"
                    + "add-shopping-cart-to-shopping_carts-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cart_items/repository/findAllByShoppingCartId/"
            + "remove-everything-after-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByShoppingCartId_ExistentShoppingCart_ReturnsListOfCartItem() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setIsbn("123456789");
        book.setDescription("Descr.");
        book.setCoverImage("Image");
        book.setPrice(BigDecimal.valueOf(99.99));
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        List<CartItem> expected = List.of(cartItem);

        Long shoppingCartId = 1L;
        List<CartItem> actual = cartItemRepository.findAllByShoppingCartId(shoppingCartId);

        assertEquals(1, actual.size());
        assertTrue(EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0), "shoppingCart"));
    }

    @Test
    @DisplayName("Find all cart-items with pageable")
    @Sql(scripts = {
            "classpath:database/cart_items/repository/findAllByShoppingCartIdWithPageable/"
                    + "add-books-to-books-table.sql",
            "classpath:database/cart_items/repository/findAllByShoppingCartIdWithPageable/"
                    + "add-cart-items-to-cart_items-table.sql",
            "classpath:database/cart_items/repository/findAllByShoppingCartIdWithPageable/"
                    + "add-shopping-cart-to-shopping_carts-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cart_items/repository/findAllByShoppingCartIdWithPageable/"
            + "remove-everything-after-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByShoppingCartId_ExistentShoppingCartWithPageable_ReturnsListOfCartItem() {
        Book firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setTitle("Title");
        firstBook.setAuthor("Author");
        firstBook.setIsbn("123456789");
        firstBook.setDescription("Descr.");
        firstBook.setCoverImage("Image");
        firstBook.setPrice(BigDecimal.valueOf(99.99));
        Book secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setTitle("Title2");
        secondBook.setAuthor("Author2");
        secondBook.setIsbn("123456789(2)");
        secondBook.setDescription("Descr.2");
        secondBook.setCoverImage("Image2");
        secondBook.setPrice(BigDecimal.valueOf(98.99));
        CartItem firstCartItem = new CartItem();
        firstCartItem.setId(1L);
        firstCartItem.setBook(firstBook);
        firstCartItem.setQuantity(2);
        CartItem secondCartItem = new CartItem();
        secondCartItem.setId(2L);
        secondCartItem.setBook(secondBook);
        secondCartItem.setQuantity(2);
        List<CartItem> expected = List.of(firstCartItem, secondCartItem);

        Pageable pageable = PageRequest.of(0, 10);
        List<CartItem> actual = cartItemRepository.findAllByShoppingCartId(1L, pageable);

        assertEquals(2, actual.size());
        assertTrue(EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0), "shoppingCart"));
        assertTrue(EqualsBuilder.reflectionEquals(expected.get(1), actual.get(1), "shoppingCart"));
    }

    @Test
    @DisplayName("findAllByShoppingCartId. Returns empty list since there is no the shopping cart")
    void findAllByShoppingCartId_NonExistentShoppingCart_ReturnsEmptyList() {
        List<CartItem> expected = Collections.emptyList();

        Long nonExistentShoppingCartId = 100L;
        List<CartItem> actual = cartItemRepository.findAllByShoppingCartId(
                nonExistentShoppingCartId
        );

        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("findAllByShoppingCartId. Returns empty list since input parameter is null")
    void findAllByShoppingCartId_NullAsInputParameter_ReturnsEmptyList() {
        List<CartItem> expected = Collections.emptyList();

        List<CartItem> actual = cartItemRepository.findAllByShoppingCartId(null);

        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("findAllByShoppingCartId. Returns empty list since there is no the shopping cart")
    void findAllByShoppingCartId_NonExistentShoppingCartWithPageable_ReturnsEmptyList() {
        List<CartItem> expected = Collections.emptyList();

        Long nonExistentShoppingCartId = 100L;
        Pageable pageable = PageRequest.of(0, 10);
        List<CartItem> actual = cartItemRepository.findAllByShoppingCartId(
                nonExistentShoppingCartId, pageable
        );

        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("findAllByShoppingCartId. Returns empty list since input parameter is null")
    void findAllByShoppingCartId_NullAsInputParameterWithPageable_ReturnsEmptyList() {
        List<CartItem> expected = Collections.emptyList();

        Pageable pageable = PageRequest.of(0, 10);
        List<CartItem> actual = cartItemRepository.findAllByShoppingCartId(null, pageable);

        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }
}
