package org.project.name.online.book.store.mapper.user;

import java.util.Set;
import org.mapstruct.Mapper;
import org.project.name.online.book.store.config.MapperConfig;
import org.project.name.online.book.store.dto.user.UserResponseDto;
import org.project.name.online.book.store.model.Role;
import org.project.name.online.book.store.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User createUser(String email, String password,
                    String firstName, String lastName,
                    String shippingAddress, Set<Role> roles);

    UserResponseDto toUserResponse(User user);
}
