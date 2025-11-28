package com.beproject.wordleapi.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WordOfTheDayResponse(
        UUID id,
        String word,
        LocalDateTime publishDate
) {
}
