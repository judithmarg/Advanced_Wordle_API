package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameEventService implements GuessHandler {

    private GuessHandler next;

    @Override
    public void setNext(GuessHandler next) {
        this.next = next;
    }

    @Override
    public ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, UUID gameSessionId, ResultGuessDTO result) {
        if(result.getStatus().equals("LOST")) {
            result.setTargetWord(target);
            return result;
        }

        boolean win = verifyWinGame(attempt, target);

        if (win) {
            result.setStatus("WIN");
            result.setTargetWord(target);
            result.setPressedLetters(pressedLetters);
            return result;
        }

        if (result.getNumberRow() >= 6) {
            result.setStatus("LOST");
            result.setTargetWord(target);
            result.setPressedLetters(pressedLetters);
            return result;
        }

        result.setStatus("IN PROGRESS");
        result.setTargetWord(null);
        result.setPressedLetters(pressedLetters);

        return result;

    }

    private boolean verifyWinGame(String attempt, String target) {
        return attempt.equals(target);
    }
}
