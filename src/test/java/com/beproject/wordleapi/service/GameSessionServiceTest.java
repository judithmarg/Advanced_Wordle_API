package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.dto.WordGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.repository.GameSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameSessionServiceTest {
    
    @Mock
    private GuessHandler guessHandler;

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private Dictionary dictionary;

    @InjectMocks
    private GameSessionServiceImpl gameSessionService;

    private GameSession getSession(String mode, String target) {
        GameSession session = new GameSession();
        session.setMode(mode);
        session.setTargetWord(target);
        session.setStatus("IN PROGRESS");
        return session;
    }

    @Test
    void dailyMode_shouldReuseExistingSession() {

        WordGuessDTO dto = new WordGuessDTO("hello", "DAILY");

        GameSession existing = getSession("DAILY", null);
        when(gameSessionRepository.findLastByModeAndStatus("DAILY", "IN PROGRESS"))
                .thenReturn(List.of(existing));

        ResultGuessDTO output = new ResultGuessDTO();
        when(guessHandler.handle(any(), any(), any(), any(), any()))
                .thenReturn(output);

        ResultGuessDTO result = gameSessionService.guessWord(dto);

        assertEquals(output, result);
        verify(dictionary, never()).getRandomWord();
        verify(gameSessionRepository, never()).save(any());
        verify(guessHandler).handle(
                eq("hello"), eq(null), eq(null), eq(existing), any(ResultGuessDTO.class)
        );
    }

    @Test
    void getRandomWord_startNewSession() {

        WordGuessDTO dto = new WordGuessDTO("hello", "RANDOM");

        when(dictionary.getRandomWord()).thenReturn("world");

        GameSession newSession = getSession("RANDOM", "world");
        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(newSession);

        ResultGuessDTO expectedResult = new ResultGuessDTO();
        expectedResult.setStatus("IN PROGRESS");

        when(guessHandler.handle(any(), any(), any(), any(), any())).thenReturn(expectedResult);
        ResultGuessDTO result = gameSessionService.guessWord(dto);

        assertNotNull(result);
        assertEquals("IN PROGRESS", result.getStatus());
        assertEquals(expectedResult, result);

        verify(gameSessionRepository).save(any(GameSession.class));
    }

    @Test
    void givenInvalidMode_shouldThrowException() {

        WordGuessDTO dto = new WordGuessDTO("hola", "INVALIDO");

        assertThrows(IllegalArgumentException.class, () -> {
            gameSessionService.guessWord(dto);
        });
    }
}
