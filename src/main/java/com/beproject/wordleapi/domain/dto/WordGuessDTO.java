package com.beproject.wordleapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record WordGuessDTO (
        @NotBlank(message = "{word.blank}")
        @Size(min = 5, max = 5, message = "{word.length}")
        @Pattern(regexp = "[A-Za-z]{5}", message = "{word.notnumbers}")
        String word,

        @NotBlank(message = "{word.blank}")
        @Pattern(regexp = "^(DAILY|RANDOM)$", message = "{mode.allowed}")
        String playMode
)
{ }
