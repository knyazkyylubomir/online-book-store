package org.project.name.online.book.store.exception.body;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorRespondBody {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private List<String> errors;
}
