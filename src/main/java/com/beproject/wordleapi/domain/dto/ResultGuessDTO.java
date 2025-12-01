package com.beproject.wordleapi.domain.dto;

import java.util.List;

public record ResultGuessDTO(
        String status,
        int numberRow,
        String wordContent,
        String resultPattern,
        List<PressedLetterDTO> pressedLetters
) {
}
