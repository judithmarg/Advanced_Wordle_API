package com.beproject.wordleapi.service;

import com.beproject.wordleapi.config.GuessChainConfig;
import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.dto.WordGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.repository.GameSessionRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameSessionServiceImpl implements GameSessionService {

    private final GuessHandler guessWordChain;
    private final DailyChallengeService dailyChallengeService;
//    private final RowPlayedService rowPlayedService;
//    private final PressedLetterService pressedLetterService;
    private GameSessionRepository  gameSessionRepository;
    private final Dictionary dictionary;

    @Override
    public ResultGuessDTO guessWord(WordGuessDTO wordGuessDTO) {

        String targetWord = getTargetWord(wordGuessDTO.playMode());
        UUID gameSessionId = startGame();
        ResultGuessDTO resultGuessDTO = new ResultGuessDTO();

        return guessWordChain.handle(
                wordGuessDTO.word(), targetWord, null, gameSessionId, resultGuessDTO
        );
    }


    private String getTargetWord(String mode) {
        if ("DAILY".equals(mode)) {
            return dailyChallengeService.getWordToday();
        }
        if ("RANDOM".equals(mode)) {
            return dictionary.getRandomWord();
        }
        throw new IllegalArgumentException("Invalid mode");
    }

    public UUID startGame() {
        // Por ahora no hay user: creamos una sesión vacía temporal
        GameSession s = new GameSession();
        gameSessionRepository.save(s);
        return s.getId();
    }
}
