package com.beproject.wordleapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WordOfTheDayRequest(
        @NotBlank
        @Size(min = 1, max = 10, message = "{word.length}")
        String word
) {
}
