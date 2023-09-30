package org.project.name.online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Authentication API", description = "Endpoints for sign in/log in")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Login to the service",
            description = "This endpoint for log in into the service")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authentication(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register to the service",
            description = "This endpoint for register into the service")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }
}
