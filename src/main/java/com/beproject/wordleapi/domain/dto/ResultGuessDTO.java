package com.beproject.wordleapi.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultGuessDTO {
        private String status;
        private int numberRow;
        private String targetWord;
        private String wordContent;
        private String resultPattern;
        private List<PressedLetterDTO> pressedLetters;
}
