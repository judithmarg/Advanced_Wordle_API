package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.repository.RowPlayedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RowPlayedServiceTest {
    @Mock
    RowPlayedRepository rowPlayedRepository;

    @Mock
    GuessHandler nextHandler;

    @InjectMocks
    RowPlayedService service;

    private GameSession session() {
        GameSession s = new GameSession();
        s.setId(UUID.randomUUID());
        return s;
    }

    @Test
    void handle_shouldGetMixedWordPatternUpdatedResult() {

        GameSession s = session();
        ResultGuessDTO result = new ResultGuessDTO();

        when(rowPlayedRepository.countByGameSession(s.getId())).thenReturn(0);

        ResultGuessDTO out = service.handle(
                "casa",
                "cama",
                null,
                s,
                result
        );

        assertEquals(1, out.getNumberRow());
        assertEquals("casa", out.getWordContent());
        assertEquals("CCWC", out.getResultPattern());

        verify(rowPlayedRepository).countByGameSession(any());
    }

    @Test
    void handle_shouldGetWrongWordPatternUpdatedResult() {

        GameSession s = session();
        ResultGuessDTO result = new ResultGuessDTO();

        when(rowPlayedRepository.countByGameSession(s.getId())).thenReturn(0);

        ResultGuessDTO out = service.handle(
                "perro",
                "magia",
                null,
                s,
                result
        );

        assertEquals(1, out.getNumberRow());
        assertEquals("perro", out.getWordContent());
        assertEquals("WWWWW", out.getResultPattern());

        verify(rowPlayedRepository).countByGameSession(any());
    }

    @Test
    void handle_shouldGetSameWordPatternUpdatedResult() {

        GameSession s = session();
        ResultGuessDTO result = new ResultGuessDTO();

        when(rowPlayedRepository.countByGameSession(s.getId())).thenReturn(0);

        ResultGuessDTO out = service.handle(
                "magia",
                "magia",
                null,
                s,
                result
        );

        assertEquals(1, out.getNumberRow());
        assertEquals("magia", out.getWordContent());
        assertEquals("CCCCC", out.getResultPattern());

        verify(rowPlayedRepository).countByGameSession(any());
    }

    @Test
    void handle_shouldReturnLostIfMaxAttemptsReached() {

        GameSession s = session();
        ResultGuessDTO result = new ResultGuessDTO();

        when(rowPlayedRepository.countByGameSession(s.getId()))
                .thenReturn(6);

        ResultGuessDTO out = service.handle(
                "casa",
                "cama",
                null,
                s,
                result
        );

        assertEquals("LOST", out.getStatus());
    }

}

