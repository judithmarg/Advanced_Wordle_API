package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.repository.DailyChallengeRepository;

import java.util.List;
import java.util.UUID;

public class DailyChallengeService implements GuessHandler{

    private GuessHandler next;
    private DailyChallengeRepository repository;

    @Override
    public void setNext(GuessHandler next) {
        this.next = next;
    }

    @Override
    public ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, UUID gameSessionId, ResultGuessDTO result) {
        if(result.getWordContent() != null && !result.getWordContent().isEmpty()) {
            return forward(attempt, target, pressedLetters, gameSessionId,result);
        }
        //later something like this for users relation
//        if(repository.hasUserPlayedToday(result.getUserId())) {
//            return result;
//        }
        // repository.saveDailyPlay(result.getUserId(), gameSessionId);
        repository.saveDailyPlay(gameSessionId);

        return forward(attempt, target, pressedLetters, gameSessionId,result);
    }

    private ResultGuessDTO forward(String attempt, String target, List<PressedLetterDTO> pressedLetters, UUID gameSessionId, ResultGuessDTO result) {
        if ( next != null ) {
            return next.handle(attempt, target, pressedLetters, gameSessionId, result);
        }
        return result;
    }
}
