package org.project.name.online.book.store.dto.book;

import jakarta.validation.constraints.Min;
import java.util.Collections;
import java.util.List;
import lombok.Data;

@Data
public class UpdateBookRequestDto {
    private String title;
    private String author;
    private String isbn;
    @Min(0)
    private Double price;
    private String description;
    private String coverImage;
    private List<Long> categoryIds = Collections.emptyList();
}
