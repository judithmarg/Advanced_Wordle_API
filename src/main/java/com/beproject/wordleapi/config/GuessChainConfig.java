package com.beproject.wordleapi.config;

import com.beproject.wordleapi.repository.GameSessionRepository;
import com.beproject.wordleapi.service.GuessHandler;
import com.beproject.wordleapi.service.PressedLetterService;
import com.beproject.wordleapi.service.RowPlayedService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GuessChainConfig {

    private final RowPlayedService rowPlayedService;
    private final PressedLetterService pressedLetterService;

    @Bean
    public GuessHandler guessChain() {
        rowPlayedService.setNext(pressedLetterService);
        pressedLetterService.setNext(null);

        return rowPlayedService;
    }
}
