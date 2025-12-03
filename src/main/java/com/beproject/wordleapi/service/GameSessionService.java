package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.dto.WordGuessDTO;

public interface GameSessionService {
    ResultGuessDTO guessWord(WordGuessDTO wordGuessDTO);
}
