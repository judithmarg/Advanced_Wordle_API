package com.beproject.wordleapi.domain.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private OffsetDateTime timestamp;
    private int status;
    private String message;
    private String error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;
    private String path;
}
