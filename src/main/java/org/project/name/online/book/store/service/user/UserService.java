package org.project.name.online.book.store.service.user;

import org.project.name.online.book.store.dto.user.UserRegistrationRequestDto;
import org.project.name.online.book.store.dto.user.UserResponseDto;
import org.project.name.online.book.store.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}
