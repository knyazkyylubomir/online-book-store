package org.project.name.online.book.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.dto.user.UserLoginRequestDto;
import org.project.name.online.book.store.dto.user.UserLoginResponseDto;
import org.project.name.online.book.store.dto.user.UserRegistrationRequestDto;
import org.project.name.online.book.store.dto.user.UserResponseDto;
import org.project.name.online.book.store.exception.RegistrationException;
import org.project.name.online.book.store.security.AuthenticationService;
import org.project.name.online.book.store.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authentication(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }
}
