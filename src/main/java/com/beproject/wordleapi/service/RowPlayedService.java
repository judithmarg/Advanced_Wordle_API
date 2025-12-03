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
    private final int MAX_ATTEMPTS = 6;

    @Override
    public void setNext(GuessHandler next) {
        this.next = next;
    }

    /**
     * This method continues with chain defining the pattern and row played in the game
     * @param attempt
     * @param target
     * @param pressedLetters
     * @param gameSession
     * @param result
     * @return a ResultGuessDTO
     */
    @Override
    public ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, GameSession gameSession, ResultGuessDTO result) {

        int totalRows = getTotalRowsPlayedByGame(gameSession.getId());
        if( totalRows >= MAX_ATTEMPTS) {
            result.setStatus("LOST");
            return result;
        }

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
     * To generate pattern according the next table:
     * C = Correct
     * M = Misplaced
     * W = Wrong
     */
    private String generatePattern(String attempt, String target) {
        char[] pattern = new char[target.length()];
        boolean[] used = new boolean[target.length()];

        IntStream.range(0, target.length()).forEach(i -> {
            if (attempt.charAt(i) == target.charAt(i)) {
                pattern[i] = 'C';
                used[i] = true;
            }
        });

        String patternFinal = IntStream.range(0, target.length())
                .mapToObj(i -> {
                    if (pattern[i] == 'C') return "C"; // ya es correcta

                    char a = attempt.charAt(i);
                    boolean found = false;
                    for (int j = 0; j < target.length(); j++) {
                        if (!used[j] && target.charAt(j) == a) {
                            found = true;
                            used[j] = true;
                            break;
                        }
                    }
                    return found ? "M" : "W";
                })
                .collect(Collectors.joining());

        return  patternFinal;
    }

    /**
     * To obtain the total rows in a defined session
     * @param gameSessionId
     * @return integer for that amount
     */
    public int getTotalRowsPlayedByGame (UUID gameSessionId) {
        return rowPlayedRepository.countByGameSession(gameSessionId);
    }

    /**
     * This method update the saving of the row played
     * @param attempt
     * @param pattern
     * @param num
     * @param gameSession
     */
    public void saveRow(String attempt, String pattern, int num, GameSession gameSession) {
        RowPlayed row = new RowPlayed();
        row.setGameSession(gameSession);
        row.setRowNumber(num);
        row.setWordContent(attempt);
        row.setResultPattern(pattern);

        rowPlayedRepository.save(row);
    }

}
