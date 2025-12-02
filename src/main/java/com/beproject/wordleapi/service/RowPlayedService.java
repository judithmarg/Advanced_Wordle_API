package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.RowPlayed;
import com.beproject.wordleapi.repository.RowPlayedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RowPlayedService implements GuessHandler {

    private GuessHandler next;
    private final RowPlayedRepository rowPlayedRepository;

    @Override
    public void setNext(GuessHandler next) {
        this.next = next;
    }

    @Override
    public ResultGuessDTO handle(String attempt, String target, UUID gameSessionId, ResultGuessDTO result) {
        int totalRows = getTotalRowsPlayedByGame(gameSessionId);
        if( totalRows >= 6) {
            result.setStatus("LOST");
            return result;
        }

        int currentRow = totalRows + 1;
        String pattern = generatePattern(attempt, target);

        saveRow(attempt, target, currentRow, gameSessionId);

        result.setNumberRow(currentRow);
        result.setWordContent(attempt);
        result.setResultPattern(pattern);

        if ( next != null) {
            next.handle(attempt, target, gameSessionId, result);
        }

        return result;
    }

    /**
     * Genera patrón tipo Wordle:
     * C = Correcto
     * M = Misplaced
     * W = Wrong
     */
    private String generatePattern(String attempt, String target) {
        String pattern = IntStream.range(0, target.length())
                .mapToObj(i -> attempt.charAt(i) == target.charAt(i) ? "C"
                        : (target.indexOf(attempt.charAt(i)) != 1) ? "M"
                        : "W")
                .collect(Collectors.joining());
        System.out.println("hi?" + pattern);

        return  pattern;
    }

    public int getTotalRowsPlayedByGame (UUID gameSessionId) {
        return rowPlayedRepository.countByGameSession(gameSessionId);
    }

    public void saveRow(String attempt, String pattern, int num, UUID gameSessionId) {
        RowPlayed row = new RowPlayed();
        row.setRowNumber(num);
        row.setWordContent(attempt);
        row.setResultPattern(pattern);

        rowPlayedRepository.save(row);
    }

}
