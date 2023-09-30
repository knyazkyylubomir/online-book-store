package org.project.name.online.book.store.service.user.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.dto.user.UserRegistrationRequestDto;
import org.project.name.online.book.store.dto.user.UserResponseDto;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.exception.RegistrationException;
import org.project.name.online.book.store.mapper.shopping.cart.ShoppingCartMapper;
import org.project.name.online.book.store.mapper.user.UserMapper;
import org.project.name.online.book.store.model.Role;
import org.project.name.online.book.store.model.ShoppingCart;
import org.project.name.online.book.store.model.User;
import org.project.name.online.book.store.repository.role.RoleRepository;
import org.project.name.online.book.store.repository.shopping.cart.ShoppingCartRepository;
import org.project.name.online.book.store.repository.user.UserRepository;
import org.project.name.online.book.store.service.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("User with given email already exists");
        }
        String email = request.getEmail();
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String shippingAddress = request.getShippingAddress();
        Role.RoleName roleName = Role.RoleName.ROLE_USER;
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(
                () -> new EntityNotFoundException("There is no role name: " + roleName));
        Set<Role> roles = Set.of(role);
        User user = userMapper.createUser(email, encodedPassword,
                firstName, lastName, shippingAddress, roles);
        ShoppingCart shoppingCart = shoppingCartMapper.setUserId(user);
        userRepository.save(user);
        shoppingCartRepository.save(shoppingCart);
        return userMapper.toUserResponse(user);
    }
}
