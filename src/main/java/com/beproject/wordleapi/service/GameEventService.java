package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameEventService implements GuessHandler {

    private GuessHandler next;

    @Override
    public void setNext(GuessHandler next) {
        this.next = next;
    }

    @Override
    public ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, GameSession gameSession, ResultGuessDTO result) {
        log.info("entra a game event con target{}", target);
        log.info("entra a game event con {}", gameSession.getStatus());
        log.info("entra a game bolean con {}", gameSession.getStatus().equals("some"));
        if(gameSession.getStatus().equals("LOST")) {
            result.setTargetWord(target);
            result.setStatus("LOST");
            return result;
        }

        log.info("luego de lost segun status {}", gameSession.getStatus());
        boolean win = verifyWinGame(attempt, target);
        log.info("luego de win segun status {}", win);


        if (win) {
            gameSession.setStatus("WIN");
            result.setStatus("WIN");
            result.setTargetWord(target);
            result.setPressedLetters(pressedLetters);
            log.info("luego de win conditional {}", result.getPressedLetters().get(0).status());
            return result;
        }

        if (result.getNumberRow() >= 6) {
            gameSession.setStatus("LOST");
            result.setStatus("LOST");
            result.setTargetWord(target);
            result.setPressedLetters(pressedLetters);
            log.info("luego de lost by attempts conditional, number presed letter {}", result.getPressedLetters());
            return result;
        }

        result.setStatus("IN PROGRESS");
        log.info("luego de win no conditional,  state {}", result.getStatus());
        result.setTargetWord(null);
        log.info("luego de win no conditional,  target {}", result.getTargetWord()); //esto solo no deberia mostrarse pero si deberia continuar con su valor
        result.setPressedLetters(pressedLetters);
        log.info("luego de win no conditional, number presed letter {}", result.getPressedLetters());

        return result;

    }

    private boolean verifyWinGame(String attempt, String target) {
        return attempt.equals(target);
    }
}
