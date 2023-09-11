package org.project.name.online.book.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.project.name.online.book.store.config.MapperConfig;
import org.project.name.online.book.store.dto.BookDto;
import org.project.name.online.book.store.dto.CreateBookRequestDto;
import org.project.name.online.book.store.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto bookDto);

    Book mergeEntities(CreateBookRequestDto bookDto, @MappingTarget Book book);
}
