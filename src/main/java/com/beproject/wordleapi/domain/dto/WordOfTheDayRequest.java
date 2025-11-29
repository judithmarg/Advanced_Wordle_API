package com.beproject.wordleapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WordOfTheDayRequest(
        @NotBlank(message = "{word.blank}")
        @Size(min = 5, max = 5, message = "{word.length}")
        String word
) {
}
