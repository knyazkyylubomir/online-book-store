package org.project.name.online.book.store.dto.cart.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCartItemRequestDto {
    @NotNull
    @Min(1)
    private Long bookId;
    @NotNull
    @Min(1)
    private Integer quantity;
}
