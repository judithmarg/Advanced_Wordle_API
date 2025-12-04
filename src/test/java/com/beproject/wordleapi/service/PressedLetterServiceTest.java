package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.domain.entity.PressedLetter;
import com.beproject.wordleapi.mapper.PressedLetterMapper;
import com.beproject.wordleapi.repository.PressedLetterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PressedLetterServiceTest {
    @Mock
    private PressedLetterRepository repository;

    @Mock
    private GuessHandler nextHandler;

    @Mock
    private PressedLetterMapper  mapper;

    @InjectMocks
    private PressedLetterService service;

    private GameSession session;

    @BeforeEach
    void setup() {
        session = new GameSession();
        session.setId(UUID.randomUUID());
    }

    @Test
    void handle_shouldSavePressedLetters() {
        String attempt = "APPLE";
        String pattern = "CMWMW";

        ResultGuessDTO result = new ResultGuessDTO();
        result.setResultPattern(pattern);

        when(repository.findByGameSessionIdAndLetter(any(), anyChar()))
                .thenReturn(Optional.empty());

        List<PressedLetter> saved = List.of(
                new PressedLetter('A', "CORRECT"),
                new PressedLetter('P', "MISPLACED")
        );

        when(repository.findByGameSessionId(session.getId()))
                .thenReturn(saved);

        when(mapper.toDto(any())).thenAnswer(inv -> {
            PressedLetter p = inv.getArgument(0);
            return new PressedLetterDTO(p.getLetter(), p.getStatus());
        });

        ResultGuessDTO response = service.handle(attempt, "APPLE", List.of(), session, result);

        verify(repository, times(5)).saveAndFlush(any());
        verify(mapper, times(2)).toDto(any());
        assertEquals(result, response);
    }

    @Test
    void handle_shouldHavePressedLetters() {
        String attempt = "APPLE";
        String pattern = "CMWMW";

        ResultGuessDTO result = new ResultGuessDTO();
        result.setResultPattern(pattern);

        when(repository.findByGameSessionIdAndLetter(any(), anyChar()))
                .thenReturn(Optional.of(new PressedLetter('A', "CORRECT")));

        List<PressedLetter> saved = List.of(
                new PressedLetter('A', "CORRECT"),
                new PressedLetter('P', "MISPLACED")
        );

        when(repository.findByGameSessionId(session.getId()))
                .thenReturn(saved);


        ResultGuessDTO response = service.handle(attempt, "APPLE", List.of(), session, result);

        verify(repository, times(5)).findByGameSessionIdAndLetter(any(),  anyChar());
        assertEquals(result, response);
    }

    @Test
    void handle_shouldNotUpdatePressedLetter_whenOldStatusIsHigher() {
        String attempt = "A";
        String pattern = "W";

        ResultGuessDTO result = new ResultGuessDTO();
        result.setResultPattern(pattern);

        PressedLetter existing = new PressedLetter('A', "CORRECT");
        existing.setGameSession(session);

        when(repository.findByGameSessionIdAndLetter(session.getId(), 'A'))
                .thenReturn(Optional.of(existing));

        when(repository.findByGameSessionId(session.getId()))
                .thenReturn(List.of(existing));

        when(mapper.toDto(existing))
                .thenReturn(new PressedLetterDTO(existing.getLetter(), existing.getStatus()));

        ResultGuessDTO response = service.handle(attempt, "A", List.of(), session, result);

        verify(repository, never()).saveAndFlush(any());
        assertEquals('A', response.getPressedLetters().get(0).letter());
        assertEquals("CORRECT", response.getPressedLetters().get(0).status());
    }


}

