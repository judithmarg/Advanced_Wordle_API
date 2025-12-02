package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;

import java.util.List;

public interface GuessHandler {
    void setNext(GuessHandler next);
    ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, GameSession gameSession, ResultGuessDTO result);
}
