package org.project.name.online.book.store.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.model.Role;
import org.project.name.online.book.store.model.ShoppingCart;
import org.project.name.online.book.store.model.User;
import org.project.name.online.book.store.repository.shopping.cart.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryIntegrationTest {
    @Autowired 
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find a shopping cart by user id")
    @Sql(scripts = {
            "classpath:database/shopping_carts/repository/"
                    + "add-shopping-cart-to-shopping_carts-table.sql",
            "classpath:database/shopping_carts/repository/"
                    + "add-user-to-users-table.sql",
            "classpath:database/shopping_carts/repository/"
                    + "add-user-role-pair-to-users_roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shopping_carts/repository/"
            + "remove-everything-after-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserId_ExistentUser_ReturnsShoppingCart() {
        ShoppingCart expected = createExpectedShoppingCart();

        Long userId = 1L;
        ShoppingCart actual = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Error!"));

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Return optional empty since user doesn't exist in DB")
    void findByUserId_NonExistentUser_ReturnsOptionalEmpty() {
        Optional<Object> expected = Optional.empty();

        Long nonExistentUserId = 100L;
        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserId(nonExistentUserId);

        assertEquals(expected, actual);
    }

    private ShoppingCart createExpectedShoppingCart() {
        Role role = new Role();
        role.setId(1L);
        role.setRoleName(Role.RoleName.ROLE_USER);
        User user = new User();
        user.setId(1L);
        user.setEmail("email@com");
        user.setPassword("1234");
        user.setFirstName("name");
        user.setLastName("last");
        user.setShippingAddress("address");
        user.setRoles(Set.of(role));
        ShoppingCart expected = new ShoppingCart();
        expected.setId(1L);
        expected.setUser(user);
        return expected;
    }
}
