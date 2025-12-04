package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.dto.WordGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.domain.entity.User;
import com.beproject.wordleapi.repository.GameSessionRepository;
import com.beproject.wordleapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSessionServiceImpl implements GameSessionService {

    @Qualifier("guessChain")
    private final GuessHandler guessWordChain;
    private final GameSessionRepository  gameSessionRepository;
    private final Dictionary dictionary;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResultGuessDTO guessWord(WordGuessDTO wordGuessDTO) {
        User currentUser = getAuthenticatedUser();

        GameSession gameSession = startGame(wordGuessDTO.playMode(), currentUser);
        
        ResultGuessDTO resultGuessDTO = new ResultGuessDTO();
        resultGuessDTO.setUserId(currentUser.getId());
        String wordUppercase = wordGuessDTO.word().toUpperCase();

        return guessWordChain.handle(
                wordUppercase, 
                gameSession.getTargetWord(),
                null, 
                gameSession, 
                resultGuessDTO
        );
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en sesión"));
    }

    /**
     * This method obtains the word if the mode is random
     * @param mode
     * @return null if the mode is daily
     */
    private String getTargetWord(String mode) {
        if ("DAILY".equals(mode)) {
            return null;
        }
        if ("RANDOM".equals(mode)) {
            return dictionary.getRandomWord();
        }
        throw new IllegalArgumentException("Invalid mode" + mode);
    }

    /**
     * This method start the game with a new session or in case of existing a current game session, continue with that
     * @param targetWord
     * @param playMode
     * @return GameSession for continue or start a game
     */
    private GameSession startGame(String playMode, User user) {
        
        List<GameSession> activeSession = gameSessionRepository.findLastByModeAndStatus(
                user.getId(), playMode, "IN_PROGRESS");

        if (!activeSession.isEmpty()) {
            log.info("Retomando sesión existente {} para {}", activeSession.get(0).getId(), user.getUsername());
            return activeSession.get(0);
        }

        if ("DAILY".equals(playMode)) {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

            boolean alreadyPlayed = gameSessionRepository.hasUserFinishedDailyGameToday(
                    user.getId(), startOfDay, endOfDay
            );

            if (alreadyPlayed) {
                throw new IllegalArgumentException("Ya has jugado tu desafío diario de hoy. ¡Vuelve mañana!");
            }
        }

        log.info("Creando nueva sesión para usuario {} en modo {}", user.getUsername(), playMode);
        
        GameSession session = new GameSession();
        session.setMode(playMode);
        session.setStatus("IN_PROGRESS");
        session.setUser(user);

        session.setTargetWord(getTargetWord(playMode)); 
        
        return gameSessionRepository.save(session);
    }
}