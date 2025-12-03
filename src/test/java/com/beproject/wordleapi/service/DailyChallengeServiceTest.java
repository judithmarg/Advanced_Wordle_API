package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.DailyChallenge;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.domain.entity.WordOfTheDay;
import com.beproject.wordleapi.repository.DailyChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyChallengeServiceTest {

    @Mock
    private DailyChallengeRepository repository;

    @Mock
    private WordOfTheDayService wordOfTheDayService;

    @Mock
    private GuessHandler nextHandler;

    @InjectMocks
    private DailyChallengeService service;

    private GameSession session;
    private ResultGuessDTO result;

    @BeforeEach
    void setup() {
        session = new GameSession();
        session.setId(UUID.randomUUID());
        session.setMode("DAILY");

        result = new ResultGuessDTO();
    }

    private WordOfTheDay buildWordOfTheDay(String word) {
        WordOfTheDay w = new WordOfTheDay();
        w.setWord(word);
        return w;
    }

    @Test
    void handle_shouldForward_whenTargetWordAlreadySet() {
        session.setTargetWord("APPLE");

        when(nextHandler.handle(eq("HELLO"), eq("APPLE"), anyList(), eq(session), eq(result)))
                .thenReturn(result);

        service.setNext(nextHandler);

        ResultGuessDTO response = service.handle("HELLO", null, List.of(), session, result);

        assertEquals(result, response);
        verify(nextHandler).handle(eq("HELLO"), eq("APPLE"), anyList(), eq(session), eq(result));
        verify(repository, never()).save(any());
    }

    @Test
    void handle_shouldCreateDailyChallenge_whenTargetWordNotSet() {
        session.setTargetWord(null);

        WordOfTheDay today = buildWordOfTheDay("BANANA");
        when(wordOfTheDayService.getTodayWord()).thenReturn(today);

        DailyChallenge savedChallenge = new DailyChallenge();
        savedChallenge.setWordOfTheDay(today);
        savedChallenge.setGameSession(session);

        when(repository.existsByGameSession(session)).thenReturn(false);
        when(repository.save(any(DailyChallenge.class))).thenReturn(savedChallenge);

        service.setNext(nextHandler);
        when(nextHandler.handle(eq("HELLO"), eq("BANANA"), anyList(), eq(session), eq(result)))
                .thenReturn(result);

        ResultGuessDTO response = service.handle("HELLO", null, List.of(), session, result);

        assertEquals(result, response);
        assertEquals("BANANA", session.getTargetWord());
        verify(repository).save(any(DailyChallenge.class));
        verify(nextHandler).handle(eq("HELLO"), eq("BANANA"), anyList(), eq(session), eq(result));
    }


    @Test
    void handle_shouldThrowException_whenDailyChallengeAlreadyExists() {
        session.setTargetWord(null);

        when(repository.existsByGameSession(session)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> service.handle("HELLO", null, List.of(), session, result));

    }

}

