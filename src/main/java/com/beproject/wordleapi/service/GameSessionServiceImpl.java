package com.beproject.wordleapi.service;

import com.beproject.wordleapi.config.GuessChainConfig;
import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.dto.WordGuessDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameSessionServiceImpl implements GameSessionService {

    private final DailyChallengeService dailyChallengeService;
    private final RowPlayedService rowPlayedService;
    private final PressedLetterService pressedLetterService;
    private final Dictionary dictionary;

    private static final int TOTAL_ROWS = 6;

//    private GuessChainConfig guessWordChain;
    @Override
    public ResultGuessDTO guessWord(WordGuessDTO wordGuessDTO) {

        ResultGuessDTO resultGuessDTO = new ResultGuessDTO();
        String wordAttempt = wordGuessDTO.word();

//        String wordTarget = getTargetWord(wordGuessDTO.playMode());

//        if(rowPlayedService.getNumRow() >= TOTAL_ROWS) {
            resultGuessDTO.setWordContent(wordAttempt);
            resultGuessDTO.setNumberRow(TOTAL_ROWS);
            resultGuessDTO.setStatus("LOST");
//            resultGuessDTO.setPressedLetters(pressedLetterService.getLetters());
            return  resultGuessDTO;
//        }
//        int currentRow = rowPlayedService.addAttempt();
//        resultGuessDTO.setNumberRow(currentRow);
//        String resultPattern = rowPlayedService.compareAttempt(wordAttempt);  //save
//
//        pressedLetterService.savePressedLetters(resultPattern, currentRow); //to add more
//        resultGuessDTO.setPressedLetters(pressedLetterService.getAllByGameSession(gameSeession));
//        long correctLength = new ArrayList<>(List.of(resultPattern)).stream().filter(l -> l == "C").count();
//
//        resultGuessDTO.setStatus(correctLength == 5 ? "CORRECT" : "PROGRESS");
//
//        return resultGuessDTO;
//            resultGuessDTO.setRowNumber(rowPlayedService.addAttempt());
//            PressedLetterDTO p = pressedLetterService.findByRow(rowPlayedService.getId());
//            List result = p.stream().filter(e -> e.equals("CORRECT")).toList;
//            if(result.length == 5) {
//                resultGuessDTO.setStatus("WIN");
//            } else {
//                resultGuessDTO.setStatus("PROGRESS");
//            }
        }


        public ResultGuessDTO guessWordChain(WordGuessDTO wordGuessDTO) {
            ResultGuessDTO resultGuessDTO = new ResultGuessDTO();
            String wordTarget = getTargetWord(wordGuessDTO.playMode());
            return guessWordChain.handle( wordGuessDTO.word(), wordTarget, startGame(),resultGuessDTO);
    }

    public UUID startGame() {
        //quiero obtener el id del juego, ahora no hay usuarios que hago?

    }
}
