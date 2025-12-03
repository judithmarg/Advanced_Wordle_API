package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.repository.GameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameEventServiceTest {
    @Mock
    GameSessionRepository gameSessionRepository;

    @InjectMocks
    GameEventService service;

    private GameSession session;
    private ResultGuessDTO result;

    @BeforeEach
    void setup() {
        session = new GameSession();
        session.setId(UUID.randomUUID());
        session.setMode("RANDOM");
        session.setStatus("IN PROGRESS");

        result = new ResultGuessDTO();
        result.setNumberRow(1);
    }

    @Test
    void handle_shouldWinGame_whenAttemptMatchesTarget() {
        String attempt = "APPLE";
        String target = "APPLE";

        ResultGuessDTO response =
                service.handle(attempt, target, null, session, result);

        assertEquals("WIN", response.getStatus());
        assertEquals(target, response.getTargetWord());

        verify(gameSessionRepository).save(session);
    }

    @Test
    void handle_shouldReturnLostDirectly_whenDailyAlreadyLost() {
        session.setMode("DAILY");
        session.setStatus("LOST");

        String attempt = "HELLO";
        String target = "APPLE";

        ResultGuessDTO response =
                service.handle(attempt, target, null, session, result);

        assertEquals("LOST", response.getStatus());
        assertEquals(target, response.getTargetWord());

        verify(gameSessionRepository, never()).save(any());
    }

    @Test
    void handle_shouldStayInProgress_whenNotWinOrLose() {
        result.setNumberRow(3);
        String attempt = "HELLO";
        String target = "APPLE";

        ResultGuessDTO response =
                service.handle(attempt, target, null, session, result);

        assertEquals("IN PROGRESS", response.getStatus());
        assertNull(response.getTargetWord());

        verify(gameSessionRepository, never()).save(any());
    }


}
