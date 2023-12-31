package org.project.name.online.book.store.dto.book;

import lombok.Data;

@Data
public class BookDtoWithoutCategoryIds {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Double price;
    private String description;
    private String coverImage;
}
