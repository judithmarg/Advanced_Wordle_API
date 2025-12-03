package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameEventService implements GuessHandler {

    private GuessHandler next;
    private final int MAX_ATTEMPTS = 6;
    private final GameSessionRepository gameSessionRepository;

    @Override
    public void setNext(GuessHandler next) {
        this.next = next;
    }

    /**
     * This method helps to define the win, lost or progressing game, comparing the data received
     * @param attempt
     * @param target
     * @param pressedLetters
     * @param gameSession
     * @param result
     * @return ResultGuessDTO
     */
    @Override
    public ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, GameSession gameSession, ResultGuessDTO result) {
        if(gameSession.getStatus().equals("LOST") && gameSession.getMode().equals("DAILY")) {
            result.setTargetWord(target);
            result.setStatus("LOST");
            return result;
        }

        boolean win = verifyWinGame(attempt, target);

        if (win || result.getNumberRow() >= MAX_ATTEMPTS) {
            gameSession.setStatus(win? "WIN":"LOST");
            gameSession.setCompletedAt(LocalDateTime.now());
            gameSessionRepository.save(gameSession);
            result.setStatus(win? "WIN":"LOST");
            result.setTargetWord(target);
            log.info("Juego concluido en sesion {}", gameSession.getId());
            return result;
        }

        result.setStatus("IN PROGRESS");
        result.setTargetWord(null);
        result.setPressedLetters(pressedLetters);

        return result;

    }

    private boolean verifyWinGame(String attempt, String target) {
        return attempt.equals(target);
    }
}
