package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.domain.entity.PressedLetter;
import com.beproject.wordleapi.mapper.PressedLetterMapper;
import com.beproject.wordleapi.repository.PressedLetterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PressedLetterService implements GuessHandler {

    private GuessHandler next;
    private final PressedLetterRepository repository;
    private final PressedLetterMapper mapper;


    @Override
    public void setNext(GuessHandler next) {
        this.next = next;
    }

    @Override
    public ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, GameSession gameSession, ResultGuessDTO result) {
        List<PressedLetterDTO> allLetters = saveAllLetters(attempt, result.getResultPattern(), gameSession);

        if( next!=null ) {
            return next.handle(attempt, target, allLetters, gameSession, result);
        }

        return result;
    }

    public List<PressedLetterDTO> saveAllLetters(String attempt, String pattern, GameSession gameSession) {
        List<String> patternRes = IntStream.range(0, pattern.length())
                        .mapToObj(i ->  pattern.charAt(i) == 'C' ? "CORRECT"
                                : pattern.charAt(i) == 'M' ? "MISPLACED"
                                : "WRONG")
                        .toList();
        List<PressedLetter> allPressed = IntStream.range(0, patternRes.size())
                .mapToObj(i -> {
                    PressedLetter p = new PressedLetter(attempt.charAt(i), patternRes.get(i));
                    p.setGameSession(gameSession);
                    return p;
                })
                .toList();
        allPressed.stream().forEach(l -> {
            Optional<PressedLetter> pressed = repository.findByGameSessionIdAndLetter(l.getGameSession().getId(), l.getLetter());
            if(pressed.isEmpty()) {
                repository.saveAndFlush(l);
            }else {
                PressedLetter p = pressed.get();
                if(!notUpdatePressedLetter(p, l)) {
                    repository.saveAndFlush(l);
                }
            }
        });
        return repository.findByGameSessionId(gameSession.getId()).stream().map(e -> mapper.toDto(e)).toList();
    }

    private boolean notUpdatePressedLetter(PressedLetter old, PressedLetter current) {
        if(old.getStatus().equals(current.getStatus())) { return true; }
        return old.getStatus().equals("CORRECT") || (old.getStatus().equals("MISPLACED") && current.getStatus().equals("WRONG"));
    }
}
