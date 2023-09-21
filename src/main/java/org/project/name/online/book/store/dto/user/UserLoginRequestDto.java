package org.project.name.online.book.store.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotEmpty
        @Size(min = 1, max = 64)
        @Email
        String email,
        @NotEmpty
        @Size(min = 8, max = 20)
        String password
) {
}
