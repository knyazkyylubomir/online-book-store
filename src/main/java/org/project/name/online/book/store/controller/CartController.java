package org.project.name.online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.dto.cart.item.CreateCartItemRequestDto;
import org.project.name.online.book.store.dto.cart.item.UpdateCartItemRequestDto;
import org.project.name.online.book.store.dto.shopping.cart.ShoppingCartDto;
import org.project.name.online.book.store.service.shopping.cart.ShoppingCartService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart API",
        description = "Endpoints for managing cart-items in a shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
@Validated
public class CartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Add a book to a shopping cart",
            description = "This endpoint adds a book to a shopping cart")
    public void addBookToCart(
            Authentication authentication,
            @RequestBody @Valid CreateCartItemRequestDto cartItemDto
    ) {
        String email = authentication.getName();
        shoppingCartService.save(email, cartItemDto);
    }

    @GetMapping
    @Operation(summary = "Receive a shopping cart with cart-items",
            description = "This endpoint receive a shopping cart with cart-items")
    public ShoppingCartDto getShoppingCart(Authentication authentication, Pageable pageable) {
        String email = authentication.getName();
        return shoppingCartService.getCartByUserId(email, pageable);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Update a quantity of cart-item by id",
            description = "This endpoint updates a quantity of a cart-item by id")
    public void updateCartItemById(
            Authentication authentication,
            @PathVariable @Min(1) Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequestDto cartItemDto
    ) {
        String email = authentication.getName();
        shoppingCartService.update(email, cartItemId, cartItemDto);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a cart-item by id",
            description = "This endpoint deletes a cart-item by id")
    public void deleteCartItemById(
            Authentication authentication,
            @PathVariable @Min(1) Long cartItemId
    ) {
        String email = authentication.getName();
        shoppingCartService.delete(email, cartItemId);
    }
}
