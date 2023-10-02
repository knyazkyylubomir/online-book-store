package org.project.name.online.book.store.mapper.shopping.cart;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.name.online.book.store.config.MapperConfig;
import org.project.name.online.book.store.dto.cart.item.CartItemDto;
import org.project.name.online.book.store.dto.shopping.cart.ShoppingCartDto;
import org.project.name.online.book.store.model.ShoppingCart;
import org.project.name.online.book.store.model.User;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "deleted")
    ShoppingCart setUserId(User user);

    @Mapping(target = "userId", source = "shoppingCart.user.id")
    @Mapping(target = "cartItems", source = "dtoList")
    ShoppingCartDto toDto(ShoppingCart shoppingCart, List<CartItemDto> dtoList);
}
