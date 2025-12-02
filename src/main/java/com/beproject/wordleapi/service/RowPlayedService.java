package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.domain.entity.RowPlayed;
import com.beproject.wordleapi.repository.RowPlayedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RowPlayedService implements GuessHandler {

    private GuessHandler next;
    private final RowPlayedRepository rowPlayedRepository;

    @Override
    public void setNext(GuessHandler next) {
        this.next = next;
    }

    @Override
    public ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, GameSession gameSession, ResultGuessDTO result) {
        log.info("llegue aqui {}",attempt );
        log.info("llegue aqui1 {}",target );
        int totalRows = getTotalRowsPlayedByGame(gameSession.getId());
        if( totalRows >= 6) {
            result.setStatus("LOST");
            return result;
        }

        log.info("llegue con gameSession {}",gameSession.getId() );
        int currentRow = totalRows + 1;
        String pattern = generatePattern(attempt, target);

        saveRow(attempt, pattern, currentRow, gameSession);

        result.setNumberRow(currentRow);
        result.setWordContent(attempt);
        result.setResultPattern(pattern);

        if ( next != null) {
            next.handle(attempt, target, pressedLetters, gameSession, result);
        }

        return result;
    }

    /**
     * Generate pattern according the next table:
     * C = Correcto
     * M = Misplaced
     * W = Wrong
     */
    private String generatePattern(String attempt, String target) {
        log.info("llegue aqui {}",target );
        String pattern = IntStream.range(0, target.length())
                .mapToObj(i -> attempt.charAt(i) == target.charAt(i) ? "C"
                        : (target.indexOf(attempt.charAt(i)) != -1) ? "M"
                        : "W")
                .collect(Collectors.joining());
        log.info("ddi {}",pattern );

        return  pattern;
    }

    public int getTotalRowsPlayedByGame (UUID gameSessionId) {
        return rowPlayedRepository.countByGameSession(gameSessionId);
    }

    public void saveRow(String attempt, String pattern, int num, GameSession gameSession) {
        RowPlayed row = new RowPlayed();
        row.setGameSession(gameSession);
        row.setRowNumber(num);
        row.setWordContent(attempt);
        row.setResultPattern(pattern);

        rowPlayedRepository.save(row);
    }

}
