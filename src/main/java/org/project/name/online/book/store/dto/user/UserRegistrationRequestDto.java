package org.project.name.online.book.store.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.project.name.online.book.store.validation.FieldMatch;

@Data
@FieldMatch.List({
        @FieldMatch(
                field = "password",
                fieldMatch = "repeatPassword"
        )
})
public class UserRegistrationRequestDto {
    @Email
    @NotBlank
    @Size(min = 1, max = 64)
    private String email;
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
    @NotBlank
    @Size(min = 8, max = 20)
    private String repeatPassword;
    @NotBlank
    @Size(min = 3, max = 50)
    private String firstName;
    @NotBlank
    @Size(min = 3, max = 50)
    private String lastName;
    @Size(min = 10, max = 100)
    private String shippingAddress;
}
