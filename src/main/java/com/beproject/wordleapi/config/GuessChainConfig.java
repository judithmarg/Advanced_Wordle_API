package com.beproject.wordleapi.config;

import com.beproject.wordleapi.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class GuessChainConfig {

    private final DailyChallengeService dailyChallengeService;
    private final RowPlayedService rowPlayedService;
    private final PressedLetterService pressedLetterService;
    private final GameEventService gameEventService;

    @Bean
    @Primary
    @Qualifier("guessChain")
    public GuessHandler guessChain() {
        dailyChallengeService.setNext(rowPlayedService);
        rowPlayedService.setNext(pressedLetterService);
        pressedLetterService.setNext(gameEventService);

        return dailyChallengeService;
    }
}
