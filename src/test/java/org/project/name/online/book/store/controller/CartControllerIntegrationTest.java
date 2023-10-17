package org.project.name.online.book.store.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.name.online.book.store.dto.cart.item.CartItemDto;
import org.project.name.online.book.store.dto.cart.item.CreateCartItemRequestDto;
import org.project.name.online.book.store.dto.cart.item.UpdateCartItemRequestDto;
import org.project.name.online.book.store.dto.shopping.cart.ShoppingCartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartControllerIntegrationTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping_carts/controller/"
                            + "add-book-to-books-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping_carts/controller/"
                            + "add-shopping-cart-to-shopping_carts-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping_carts/controller/"
                            + "add-user-to-users-table.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping_carts/controller/"
                            + "remove-everything-after-tests.sql")
            );
        }
    }

    @WithMockUser(username = "email@com", roles = "USER")
    @Test
    @Sql(scripts = "classpath:database/shopping_carts/controller/addBookToCart/"
            + "remove-cart-items-before-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Add a new cart-item to shopping cart")
    void addBookToCart_ValidRequestDto_RespondStatusAccepted() throws Exception {
        CreateCartItemRequestDto requestDto = createCartItemRequest();
        ShoppingCartDto expected = createExpectedShoppingCartDtoWithBookIdTwo();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "cartItems"));
        assertTrue(EqualsBuilder.reflectionEquals(
                expected.getCartItems().get(0), actual.getCartItems().get(0), "id")
        );
    }

    @WithMockUser(username = "email@com", roles = "USER")
    @Test
    @Sql(scripts = "classpath:database/shopping_carts/controller/getShoppingCart/"
            + "add-cart-item-to-cart_items.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Get a shopping cart")
    void getShoppingCart_WhichExistInDb_RespondStatusOk() throws Exception {
        ShoppingCartDto expected = createExpectedShoppingCartDtoWithBookIdOne();

        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "email@com", roles = "USER")
    @Test
    @Sql(scripts = "classpath:database/shopping_carts/controller/updateCartItemById/"
            + "add-cart-item-to-cart_items-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Update a quantity of the cart-item by cart-item id")
    void updateCartItemById_ValidRequestDto_RespondStatusAccepted() throws Exception {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(25);
        ShoppingCartDto expected = createExpectedShoppingCartDtoWithBookIdThree();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(put("/cart/cart-items/3")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "email@com", roles = "USER")
    @Test
    @Sql(scripts = "classpath:database/shopping_carts/controller/deleteCartItemById/"
            + "add-cart-item-to-cart_items.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Delete a cart-item by cart-item id")
    void deleteCartItemById_ValidRequestDto_RespondStatusNoContent() throws Exception {
        ShoppingCartDto expected = createExpectedShoppingCartDtoWithEmptyCartItems();

        mockMvc.perform(delete("/cart/cart-items/4"))
                .andExpect(status().isNoContent());
        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "email@com", roles = "USER")
    @Test
    @DisplayName("addBookToCart. Receive bad request status since dto isn't valid")
    void addBookToCart_NotValidRequestDto_RespondStatusBadRequest() throws Exception {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "email@com", roles = "USER")
    @Test
    @DisplayName("updateCartItemById. Receive bad request status since dto isn't valid")
    void updateCartItemById_NotValidRequestDto_RespondStatusBadRequest() throws Exception {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/cart/cart-items/3")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "email@com", roles = "USER")
    @Test
    @DisplayName("updateCartItemById. Receive bad request status since id is less than 1")
    void updateCartItemById_NotValidId_RespondStatusBadRequest() throws Exception {
        mockMvc.perform(put("/cart/cart-items/0"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "email@com", roles = "USER")
    @Test
    @DisplayName("deleteCartItemById. Receive bad request status since id is less than 1")
    void deleteCartItemById_NotValidId_RespondStatusBadRequest() throws Exception {
        mockMvc.perform(delete("/cart/cart-items/0"))
                .andExpect(status().isBadRequest());
    }

    private CreateCartItemRequestDto createCartItemRequest() {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(2L);
        requestDto.setQuantity(1);
        return requestDto;
    }

    private ShoppingCartDto createExpectedShoppingCartDtoWithBookIdTwo() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setBookId(2L);
        cartItemDto.setBookTitle("Title2");
        cartItemDto.setQuantity(1);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        expected.setUserId(1L);
        expected.setCartItems(List.of(cartItemDto));
        return expected;
    }

    private ShoppingCartDto createExpectedShoppingCartDtoWithBookIdOne() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);
        cartItemDto.setBookId(1L);
        cartItemDto.setBookTitle("Title1");
        cartItemDto.setQuantity(10);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        expected.setUserId(1L);
        expected.setCartItems(List.of(cartItemDto));
        return expected;
    }

    private ShoppingCartDto createExpectedShoppingCartDtoWithBookIdThree() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(3L);
        cartItemDto.setBookId(3L);
        cartItemDto.setBookTitle("Title3");
        cartItemDto.setQuantity(25);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        expected.setUserId(1L);
        expected.setCartItems(List.of(cartItemDto));
        return expected;
    }

    private ShoppingCartDto createExpectedShoppingCartDtoWithEmptyCartItems() {
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        expected.setUserId(1L);
        expected.setCartItems(Collections.emptyList());
        return expected;
    }
}
