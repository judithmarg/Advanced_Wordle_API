package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.PressedLetter;
import com.beproject.wordleapi.domain.entity.RowPlayed;
import com.beproject.wordleapi.repository.PressedLetterRepository;
import com.beproject.wordleapi.repository.RowPlayedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PressedLetterService implements GuessHandler {

    private GuessHandler next;
    private PressedLetterRepository repository;

    @Override
    public void setNext(GuessHandler next) {
        this.next = next;
    }

    @Override
    public ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, UUID gameSessionId, ResultGuessDTO result) {
        saveAllLetters(attempt, target, gameSessionId);

        if( next!=null ) {
            return next.handle(attempt, target, pressedLetters, gameSessionId, result);
        }

        return result;
    }

    public void saveAllLetters(String attempt, String target,  UUID gameSessionId) {
        List<String> patternRes = IntStream.range(0, target.length())
                        .mapToObj(i ->  attempt.charAt(i) == target.charAt(i) ? "CORRECT"
                                : (target.indexOf(attempt.charAt(i)) != -1) ? "MISPLACED"
                                : "WRONG")
                        .toList();
        //logic to repeated letters
        IntStream.range(0, patternRes.size())
                        .forEach(index ->
                        {repository.save(new PressedLetter(attempt.charAt(index), patternRes.get(index)))
                        });

    }
}
