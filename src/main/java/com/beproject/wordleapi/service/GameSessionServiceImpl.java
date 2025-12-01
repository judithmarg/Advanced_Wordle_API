package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.dto.WordGuessDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameSessionServiceImpl implements GameSessionService {
    @Override
    public ResultGuessDTO guessWord(WordGuessDTO wordGuessDTO) {
        return null;
    }
}
