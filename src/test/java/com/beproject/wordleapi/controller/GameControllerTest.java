package com.beproject.wordleapi.controller;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.dto.WordGuessDTO;
import com.beproject.wordleapi.service.GameSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GameControllerTest {

    @Mock
    private GameSessionService service;

    @InjectMocks
    private GameController controller;

    private WordGuessDTO guessDTO;
    private ResultGuessDTO resultDTO;

    @BeforeEach
    void setUp() {
        guessDTO = new WordGuessDTO("nuevo", "DAILY");
        resultDTO = new ResultGuessDTO("IN_PROGRESS",1,"NUEVO","SMTHP","WWWWW", null, 1L);
    }

    @Test
    void shouldReturnGuessResult() {
        when(service.guessWord(guessDTO)).thenReturn(resultDTO);

        ResponseEntity<ResultGuessDTO> result = controller.guessWord(guessDTO);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(resultDTO, result.getBody());
    }

    @Test
    void tryGuessWhenModeIsInvalid() {
        WordGuessDTO result2  = new WordGuessDTO("nuevo", "OTHER");
        when(service.guessWord(result2))
                .thenThrow(new IllegalArgumentException("Invalid play mode"));

        assertThrows(IllegalArgumentException.class, () -> controller.guessWord(result2));
    }

    @Test
    void shouldReturnGuessResultLost() {
        ResultGuessDTO result = new ResultGuessDTO("LOST",6,"NUEVO","SMTHP","WWWWW", null, 1L);
        when(service.guessWord(guessDTO)).thenReturn(result);

        ResponseEntity<ResultGuessDTO> res = controller.guessWord(guessDTO);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(result, res.getBody());
    }
}
