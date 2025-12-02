package com.beproject.wordleapi.config;

import com.beproject.wordleapi.repository.GameSessionRepository;
import com.beproject.wordleapi.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GuessChainConfig {

    private final DailyChallengeService dailyChallengeService;
    private final RowPlayedService rowPlayedService;
    private final PressedLetterService pressedLetterService;
    private final GameEventService gameEventService;

    @Bean
    public GuessHandler guessChain() {
        dailyChallengeService.setNext(rowPlayedService);
        rowPlayedService.setNext(pressedLetterService);
        pressedLetterService.setNext(gameEventService);

        return dailyChallengeService;
    }
}
