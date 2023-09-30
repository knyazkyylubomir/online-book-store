package org.project.name.online.book.store.mapper.cart.item;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.name.online.book.store.config.MapperConfig;
import org.project.name.online.book.store.dto.cart.item.CartItemDto;
import org.project.name.online.book.store.dto.cart.item.CreateCartItemRequestDto;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.model.CartItem;
import org.project.name.online.book.store.model.ShoppingCart;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "deleted")
    CartItem toEntity(
            ShoppingCart shoppingCart,
            Book book,
            CreateCartItemRequestDto cartItemDto);

    @Mapping(target = "bookId", source = "cartItem.book.id")
    @Mapping(target = "bookTitle", source = "cartItem.book.title")
    CartItemDto toDto(CartItem cartItem);

    List<CartItemDto> toDtoList(List<CartItem> cartItems);
}
