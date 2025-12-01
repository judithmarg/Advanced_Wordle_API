package com.beproject.wordleapi.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

public record WordOfTheDayResponse(
        UUID id,
        String word,
        LocalDate publishDate
) {
}
