package org.project.name.online.book.store.mapper.error.respond.body;

import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Mapper;
import org.project.name.online.book.store.config.MapperConfig;
import org.project.name.online.book.store.model.ErrorRespondBody;
import org.springframework.http.HttpStatus;

@Mapper(config = MapperConfig.class)
public interface ErrorRespondBodyMapper {
    ErrorRespondBody createErrorBody(
            LocalDateTime timestamp, HttpStatus status, List<String> errors);
}
