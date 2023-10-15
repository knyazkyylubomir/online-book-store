package org.project.name.online.book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.name.online.book.store.dto.cart.item.CartItemDto;
import org.project.name.online.book.store.dto.cart.item.CreateCartItemRequestDto;
import org.project.name.online.book.store.dto.cart.item.UpdateCartItemRequestDto;
import org.project.name.online.book.store.dto.shopping.cart.ShoppingCartDto;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.mapper.cart.item.CartItemMapper;
import org.project.name.online.book.store.mapper.shopping.cart.ShoppingCartMapper;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.model.CartItem;
import org.project.name.online.book.store.model.Category;
import org.project.name.online.book.store.model.Role;
import org.project.name.online.book.store.model.ShoppingCart;
import org.project.name.online.book.store.model.User;
import org.project.name.online.book.store.repository.book.BookRepository;
import org.project.name.online.book.store.repository.cart.item.CartItemRepository;
import org.project.name.online.book.store.repository.shopping.cart.ShoppingCartRepository;
import org.project.name.online.book.store.repository.user.UserRepository;
import org.project.name.online.book.store.service.shopping.cart.impl.ShoppingCartServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    private static Role role;
    private static User user;
    private static ShoppingCart shoppingCart;
    private static Category category;
    private static Book book;
    private static CartItem cartItem;
    private static User userForTestingExceptions;
    private static ShoppingCart shoppingCartWithId;
    private static Book firstBook;
    private static Book secondBook;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @BeforeAll
    static void beforeAll() {
        role = new Role();
        role.setId(1L);
        role.setRoleName(Role.RoleName.ROLE_USER);
        user = new User();
        user.setId(1L);
        user.setEmail("email@com");
        user.setPassword("1234");
        user.setFirstName("name");
        user.setLastName("last");
        user.setShippingAddress("address");
        user.setRoles(Set.of(role));
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        category = new Category();
        category.setId(1L);
        category.setName("Test name");
        category.setDescription("Test name descr.");
        book = new Book();
        book.setId(1L);
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setIsbn("123456789");
        book.setPrice(BigDecimal.valueOf(99.99));
        book.setDescription("Descr.");
        book.setCoverImage("Image");
        book.setCategories(Set.of(category));
        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(3);
        userForTestingExceptions = new User();
        userForTestingExceptions.setId(1L);
        userForTestingExceptions.setEmail("email@com");
        shoppingCartWithId = new ShoppingCart();
        shoppingCartWithId.setId(1L);
        shoppingCartWithId.setUser(user);
        shoppingCartWithId.setCartItems(Set.of(cartItem));
        firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setTitle("Title");
        firstBook.setAuthor("Author");
        firstBook.setIsbn("123456789");
        firstBook.setPrice(BigDecimal.valueOf(99.99));
        firstBook.setDescription("Descr.");
        firstBook.setCoverImage("Image");
        firstBook.setCategories(Set.of(category));
        secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setTitle("Title1");
        secondBook.setAuthor("Author1");
        secondBook.setIsbn("123456789(1)");
        secondBook.setPrice(BigDecimal.valueOf(98.99));
        secondBook.setDescription("Descr.1");
        secondBook.setCoverImage("Image1");
        secondBook.setCategories(Set.of(category));
    }

    @Test
    @DisplayName("Save a cart-item in user's shopping cart")
    void save_WithValidFields_ReturnsVoid() {
        CreateCartItemRequestDto inputDto = createCartItemRequest();
        CartItem cartItem = createCartItem();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(
                Optional.of(shoppingCart)
        );
        when(cartItemRepository.findAllByShoppingCartId(shoppingCart.getId())).thenReturn(
                Collections.emptyList()
        );
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(cartItemMapper.toEntity(shoppingCart, book, inputDto)).thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        shoppingCartService.save(user.getEmail(), inputDto);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
        verify(cartItemRepository, times(1)).findAllByShoppingCartId(shoppingCart.getId());
        verify(bookRepository, times(1)).findById(book.getId());
        verify(cartItemMapper, times(1)).toEntity(shoppingCart, book, inputDto);
        verify(cartItemRepository, times(1)).save(cartItem);
        verifyNoMoreInteractions(
                userRepository,
                shoppingCartRepository,
                cartItemRepository,
                bookRepository,
                cartItemMapper
        );
    }

    @Test
    @DisplayName("Update a quantity of the cart-item since it's already represented")
    void save_WithDuplicateCartItem_ReturnsVoid() {
        ShoppingCart shoppingCart = createShoppingCart();
        CartItem fitstCartItem = createFirstCartItem(shoppingCart);
        CartItem secondCartItem = createSecondCartItem(shoppingCart);
        shoppingCart.setCartItems(Set.of(fitstCartItem, secondCartItem));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(
                Optional.of(shoppingCart)
        );
        when(cartItemRepository.findAllByShoppingCartId(shoppingCart.getId())).thenReturn(
                List.of(fitstCartItem, secondCartItem)
        );
        when(cartItemRepository.save(secondCartItem)).thenReturn(secondCartItem);

        CreateCartItemRequestDto inputDto = createCartItemRequestWithQuantityTwo();
        shoppingCartService.save(user.getEmail(), inputDto);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
        verify(cartItemRepository, times(1)).findAllByShoppingCartId(shoppingCart.getId());
        verify(cartItemRepository, times(1)).save(secondCartItem);
        verifyNoMoreInteractions(userRepository, shoppingCartRepository, cartItemRepository);
    }

    @Test
    @DisplayName("Get a shopping cart by user")
    void getCartByUserId_WhichPersistInDb_ReturnsShoppingCartDto() {
        CartItemDto cartItemDto = createCartItemDto();
        List<CartItemDto> cartItemsDto = List.of(cartItemDto);
        ShoppingCartDto expected = createExpectedShoppingCartDto(cartItemsDto);
        Pageable pageable = PageRequest.of(0, 10);
        List<CartItem> cartItems = List.of(cartItem);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(
                Optional.of(shoppingCartWithId)
        );
        when(cartItemRepository.findAllByShoppingCartId(shoppingCartWithId.getId(), pageable))
                .thenReturn(cartItems);
        when(cartItemMapper.toDtoList(cartItems)).thenReturn(cartItemsDto);
        when(shoppingCartMapper.toDto(shoppingCartWithId, cartItemsDto)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.getCartByUserId(user.getEmail(), pageable);

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
        verify(cartItemRepository, times(1)).findAllByShoppingCartId(
                shoppingCartWithId.getId(), pageable
        );
        verify(cartItemMapper, times(1)).toDtoList(cartItems);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCartWithId, cartItemsDto);
        verifyNoMoreInteractions(
                userRepository,
                shoppingCartRepository,
                cartItemRepository,
                cartItemMapper,
                shoppingCartMapper
        );
    }

    @Test
    @DisplayName("Update a cart-item by cart-item id")
    void update_WhichPersistInDb_ReturnsVoid() {
        UpdateCartItemRequestDto inputDto = new UpdateCartItemRequestDto();
        inputDto.setQuantity(10);
        ShoppingCart shoppingCart = createShoppingCart();
        CartItem firstCartItem = createFirstCartItem(shoppingCart);
        CartItem secondCartItem = createSecondCartItem(shoppingCart);
        shoppingCart.setCartItems(Set.of(firstCartItem, secondCartItem));
        CartItem updatedCartItem = createUpdatedCartItem(secondBook, shoppingCart);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(
                Optional.of(shoppingCart)
        );
        List<CartItem> cartItems = List.of(firstCartItem, secondCartItem);
        when(cartItemRepository.findAllByShoppingCartId(shoppingCart.getId())).thenReturn(
                cartItems
        );
        when(cartItemRepository.save(updatedCartItem)).thenReturn(updatedCartItem);

        Long cartItemId = 2L;
        shoppingCartService.update(user.getEmail(), cartItemId, inputDto);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
        verify(cartItemRepository, times(1)).findAllByShoppingCartId(shoppingCart.getId());
        verify(cartItemRepository, times(1)).save(updatedCartItem);
        verifyNoMoreInteractions(userRepository, shoppingCartRepository, cartItemRepository);
    }

    @Test
    @DisplayName("Delete a cart-item by id")
    void delete_WhichPersistInDb_ReturnsVoid() {
        List<CartItem> cartItems = List.of(cartItem);
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(
                Optional.of(shoppingCartWithId)
        );
        when(cartItemRepository.findAllByShoppingCartId(shoppingCartWithId.getId())).thenReturn(
                cartItems
        );

        shoppingCartService.delete(user.getEmail(), cartItem.getId());

        verify(cartItemRepository, times(1)).findById(cartItem.getId());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
        verify(cartItemRepository, times(1)).findAllByShoppingCartId(shoppingCartWithId.getId());
        verify(cartItemRepository, times(1)).delete(cartItem);
        verifyNoMoreInteractions(cartItemRepository, userRepository, shoppingCartRepository);
    }

    @Test
    @DisplayName("save. Throw EntityNotFoundException since the user doesn't exist")
    void save_WithNonExistentUser_ThrowsEntityNotFoundException() {
        CreateCartItemRequestDto inputDto = new CreateCartItemRequestDto();
        String email = "doesn'texist@com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.save(email, inputDto)
        );
        String expected = "There is no user by email: " + email;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("save. Throw EntityNotFoundException since the shopping cart doesn't exist")
    void save_WithNonExistentShoppingCart_ThrowsEntityNotFoundException() {
        when(userRepository.findByEmail(userForTestingExceptions.getEmail())).thenReturn(
                Optional.of(userForTestingExceptions)
        );
        when(shoppingCartRepository.findByUserId(userForTestingExceptions.getId())).thenReturn(
                Optional.empty()
        );

        CreateCartItemRequestDto inputDto = new CreateCartItemRequestDto();
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.save(userForTestingExceptions.getEmail(), inputDto)
        );
        String expected = "There is no shopping cart by user id: "
                + userForTestingExceptions.getId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findByEmail(userForTestingExceptions.getEmail());
        verify(shoppingCartRepository, times(1)).findByUserId(userForTestingExceptions.getId());
        verifyNoMoreInteractions(userRepository, shoppingCartRepository);
    }

    @Test
    @DisplayName("save. Throw EntityNotFoundException since the book doesn't exist")
    void save_WithNonExistentBook_ThrowsEntityNotFoundException() {
        CreateCartItemRequestDto inputDto = new CreateCartItemRequestDto();
        inputDto.setBookId(100L);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        when(userRepository.findByEmail(userForTestingExceptions.getEmail())).thenReturn(
                Optional.of(userForTestingExceptions)
        );
        when(shoppingCartRepository.findByUserId(userForTestingExceptions.getId())).thenReturn(
                Optional.of(shoppingCart)
        );
        when(cartItemRepository.findAllByShoppingCartId(shoppingCart.getId())).thenReturn(
                Collections.emptyList()
        );
        when(bookRepository.findById(inputDto.getBookId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.save(userForTestingExceptions.getEmail(), inputDto)
        );
        String expected = "There is no book by id: " + inputDto.getBookId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findByEmail(userForTestingExceptions.getEmail());
        verify(shoppingCartRepository, times(1)).findByUserId(userForTestingExceptions.getId());
        verify(cartItemRepository, times(1)).findAllByShoppingCartId(shoppingCart.getId());
        verify(bookRepository, times(1)).findById(inputDto.getBookId());
        verifyNoMoreInteractions(
                bookRepository,
                userRepository,
                shoppingCartRepository,
                cartItemRepository
        );
    }

    @Test
    @DisplayName("getCartByUserId. Throw EntityNotFoundException since the user doesn't exist")
    void getCartByUserId_WithNonExistentUser_ThrowsEntityNotFoundException() {
        String email = "doesn'texist@com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Pageable pageable = PageRequest.of(0, 10);
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.getCartByUserId(email, pageable)
        );
        String expected = "There is no user by email: " + email;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName(
            "getCartByUserId. Throw EntityNotFoundException since the shopping cart doesn't exist"
    )
    void getCartByUserId_WithNonExistentShoppingCart_ThrowsEntityNotFoundException() {
        when(userRepository.findByEmail(userForTestingExceptions.getEmail())).thenReturn(
                Optional.of(userForTestingExceptions)
        );
        when(shoppingCartRepository.findByUserId(userForTestingExceptions.getId())).thenReturn(
                Optional.empty()
        );

        Pageable pageable = PageRequest.of(0, 10);
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.getCartByUserId(
                        userForTestingExceptions.getEmail(), pageable
                )
        );
        String expected = "There is no shopping cart by user id: "
                + userForTestingExceptions.getId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findByEmail(userForTestingExceptions.getEmail());
        verify(shoppingCartRepository, times(1)).findByUserId(userForTestingExceptions.getId());
        verifyNoMoreInteractions(userRepository, shoppingCartRepository);
    }

    @Test
    @DisplayName("update. Throw EntityNotFoundException since the user doesn't exist")
    void update_WithNonExistentUser_ThrowsEntityNotFoundException() {
        String email = "doesn'texist@com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Long cartItemId = 1L;
        UpdateCartItemRequestDto inputDto = new UpdateCartItemRequestDto();
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.update(email, cartItemId, inputDto)
        );
        String expected = "There is no user by email: " + email;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("update. Throw EntityNotFoundException since the shopping cart doesn't exist")
    void update_WithNonExistentShoppingCart_ThrowsEntityNotFoundException() {
        when(userRepository.findByEmail(userForTestingExceptions.getEmail())).thenReturn(
                Optional.of(userForTestingExceptions)
        );
        when(shoppingCartRepository.findByUserId(userForTestingExceptions.getId())).thenReturn(
                Optional.empty()
        );

        Long cartItemId = 1L;
        UpdateCartItemRequestDto inputDto = new UpdateCartItemRequestDto();
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.update(
                        userForTestingExceptions.getEmail(), cartItemId, inputDto
                )
        );
        String expected = "There is no shopping cart by user id: "
                + userForTestingExceptions.getId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findByEmail(userForTestingExceptions.getEmail());
        verify(shoppingCartRepository, times(1)).findByUserId(userForTestingExceptions.getId());
        verifyNoMoreInteractions(userRepository, shoppingCartRepository);
    }

    @Test
    @DisplayName("update. Throw EntityNotFoundException since the cart-item doesn't exist")
    void update_WithNonExistentCartItem_ThrowsEntityNotFoundException() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        when(userRepository.findByEmail(userForTestingExceptions.getEmail())).thenReturn(
                Optional.of(userForTestingExceptions)
        );
        when(shoppingCartRepository.findByUserId(userForTestingExceptions.getId())).thenReturn(
                Optional.of(shoppingCart)
        );
        when(cartItemRepository.findAllByShoppingCartId(shoppingCart.getId())).thenReturn(
                Collections.emptyList()
        );

        Long cartItemId = 100L;
        UpdateCartItemRequestDto inputDto = new UpdateCartItemRequestDto();
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.update(
                        userForTestingExceptions.getEmail(), cartItemId, inputDto
                )
        );
        String expected = "There is no cart-item by id: " + cartItemId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findByEmail(userForTestingExceptions.getEmail());
        verify(shoppingCartRepository, times(1)).findByUserId(userForTestingExceptions.getId());
        verify(cartItemRepository, times(1)).findAllByShoppingCartId(shoppingCart.getId());
        verifyNoMoreInteractions(userRepository, shoppingCartRepository, cartItemRepository);
    }

    @Test
    @DisplayName("delete. Throw EntityNotFoundException since the cart-item doesn't exist")
    void delete_WithNonExistentCartItem_ThrowsEntityNotFoundException() {
        Long nonExistentCartItemId = 100L;
        when(cartItemRepository.findById(nonExistentCartItemId)).thenReturn(Optional.empty());

        String email = "email@com";
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.delete(email, nonExistentCartItemId)
        );
        String expected = "There is no cart-item by id: " + nonExistentCartItemId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).findById(nonExistentCartItemId);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("delete. Throw EntityNotFoundException since the user doesn't exist")
    void delete_WithNonExistentUser_ThrowsEntityNotFoundException() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        String email = "doesn'texist@com";
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.delete(email, cartItem.getId())
        );
        String expected = "There is no user by email: " + email;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).findById(cartItem.getId());
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(cartItemRepository, userRepository);
    }

    @Test
    @DisplayName("delete. Throw EntityNotFoundException since the shopping cart doesn't exist")
    void delete_WithNonExistentShoppingCart_ThrowsEntityNotFoundException() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        User user = new User();
        user.setId(1L);
        String email = "doesn'texist@com";
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.delete(email, cartItem.getId())
        );
        String expected = "There is no shopping cart by user id: " + user.getId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).findById(cartItem.getId());
        verify(userRepository, times(1)).findByEmail(email);
        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
        verifyNoMoreInteractions(cartItemRepository, userRepository, shoppingCartRepository);
    }

    private CreateCartItemRequestDto createCartItemRequest() {
        CreateCartItemRequestDto inputDto = new CreateCartItemRequestDto();
        inputDto.setBookId(1L);
        inputDto.setQuantity(1);
        return inputDto;
    }

    private CartItem createCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        return cartItem;
    }

    private CreateCartItemRequestDto createCartItemRequestWithQuantityTwo() {
        CreateCartItemRequestDto inputDto = new CreateCartItemRequestDto();
        inputDto.setBookId(2L);
        inputDto.setQuantity(2);
        return inputDto;
    }

    private ShoppingCart createShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        return shoppingCart;
    }

    private CartItem createCartItemWithId(ShoppingCart shoppingCart) {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        cartItem.setShoppingCart(shoppingCart);
        return cartItem;
    }

    private CartItemDto createCartItemDto() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);
        cartItemDto.setBookId(1L);
        cartItemDto.setBookTitle("Title");
        cartItemDto.setQuantity(3);
        return cartItemDto;
    }

    private ShoppingCartDto createExpectedShoppingCartDto(List<CartItemDto> cartItemsDto) {
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        expected.setUserId(1L);
        expected.setCartItems(cartItemsDto);
        return expected;
    }

    private CartItem createFirstCartItem(ShoppingCart shoppingCart) {
        CartItem firstCartItem = new CartItem();
        firstCartItem.setId(1L);
        firstCartItem.setBook(firstBook);
        firstCartItem.setQuantity(1);
        firstCartItem.setShoppingCart(shoppingCart);
        return firstCartItem;
    }

    private CartItem createSecondCartItem(ShoppingCart shoppingCart) {
        CartItem secondCartItem = new CartItem();
        secondCartItem.setId(2L);
        secondCartItem.setBook(secondBook);
        secondCartItem.setQuantity(1);
        secondCartItem.setShoppingCart(shoppingCart);
        return secondCartItem;
    }

    private CartItem createUpdatedCartItem(Book secondBook, ShoppingCart shoppingCart) {
        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setId(2L);
        updatedCartItem.setBook(secondBook);
        updatedCartItem.setQuantity(10);
        updatedCartItem.setShoppingCart(shoppingCart);
        return updatedCartItem;
    }
}
