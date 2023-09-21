package org.project.name.online.book.store.mapper.book;

import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.project.name.online.book.store.config.MapperConfig;
import org.project.name.online.book.store.dto.book.BookDto;
import org.project.name.online.book.store.dto.book.CreateBookRequestDto;
import org.project.name.online.book.store.exception.body.ErrorRespondBody;
import org.project.name.online.book.store.model.Book;
import org.springframework.http.HttpStatus;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto bookDto);

    Book mergeEntities(CreateBookRequestDto bookDto, @MappingTarget Book book);

    ErrorRespondBody createErrorBody(
            LocalDateTime timestamp, HttpStatus status, List<String> errors);
}
