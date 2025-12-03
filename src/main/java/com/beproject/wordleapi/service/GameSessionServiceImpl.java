package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.dto.WordGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSessionServiceImpl implements GameSessionService {

    @Qualifier("guessChain")
    private final GuessHandler guessWordChain;
    private final GameSessionRepository  gameSessionRepository;
    private final Dictionary dictionary;

    @Override
    public ResultGuessDTO guessWord(WordGuessDTO wordGuessDTO) {

        String targetWord = getTargetWord(wordGuessDTO.playMode());
        GameSession gameSession = startGame(targetWord,wordGuessDTO.playMode());
        ResultGuessDTO resultGuessDTO = new ResultGuessDTO();

        return guessWordChain.handle(
                wordGuessDTO.word(), targetWord, null, gameSession, resultGuessDTO
        );
    }


    private String getTargetWord(String mode) {
        if ("DAILY".equals(mode)) {
            return null;
        }
        if ("RANDOM".equals(mode)) {
            return dictionary.getRandomWord();
        }
        throw new IllegalArgumentException("Invalid mode");
    }

    public GameSession startGame(String targetWord, String playMode) {

        //here filtrar gameSession de un usuario del dia de hoy y luego lo de abajo
        log.info("Juego iniciado en modo {}", playMode);
        List<GameSession> lastSession = gameSessionRepository.findLastByModeAndStatus(playMode, "IN PROGRESS");

        if (!lastSession.isEmpty()) {
            return lastSession.get(0);
        }

        GameSession session = new GameSession();
        session.setMode(playMode);
        session.setTargetWord(targetWord);
        session.setStatus("IN PROGRESS");
        gameSessionRepository.save(session);
        return session;
    }
}
