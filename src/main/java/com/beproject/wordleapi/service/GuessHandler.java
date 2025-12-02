package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;

import java.util.UUID;

public interface GuessHandler {
    void setNext(GuessHandler next);
    ResultGuessDTO handle(String attempt, String target, UUID gameSessionId, ResultGuessDTO result);
}
