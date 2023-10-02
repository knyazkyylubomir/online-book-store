package org.project.name.online.book.store.mapper.book;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.project.name.online.book.store.config.MapperConfig;
import org.project.name.online.book.store.dto.book.BookDto;
import org.project.name.online.book.store.dto.book.BookDtoWithoutCategoryIds;
import org.project.name.online.book.store.dto.book.CreateBookRequestDto;
import org.project.name.online.book.store.dto.book.UpdateBookRequestDto;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.model.Category;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto bookDto, List<Category> categories);

    @Mapping(ignore = true, target = "categories")
    Book mergeEntities(UpdateBookRequestDto bookDto,
                       @MappingTarget Book book, List<Category> categories);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        List<String> categoryIds = new ArrayList<>();
        for (Category category : book.getCategories()) {
            categoryIds.add(category.getName());
        }
        bookDto.setCategoryIds(categoryIds);
    }

    @AfterMapping
    default void setCategoryIds(@MappingTarget Book book, List<Category> categories) {
        if (!categories.isEmpty()) {
            Set<Category> categorySet = new HashSet<>(categories);
            book.setCategories(categorySet);
        }
    }
}
