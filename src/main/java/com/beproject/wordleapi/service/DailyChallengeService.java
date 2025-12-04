package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.entity.DailyChallenge;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.domain.entity.WordOfTheDay;
import com.beproject.wordleapi.repository.DailyChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyChallengeService implements GuessHandler{

    private GuessHandler next;
    private final DailyChallengeRepository repository;
    private final WordOfTheDayService  wordOfTheDayService;

    @Override
    public void setNext(GuessHandler next) {
        this.next = next;
    }

    /**
     * This method updates the target word if daily mode was selected
     * @param attempt
     * @param target
     * @param pressedLetters
     * @param gameSession
     * @param result
     * @return ResultGuessDTO
     */
    @Override
    public ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, GameSession gameSession, ResultGuessDTO result) {

        if(gameSession.getTargetWord() != null &&  !gameSession.getTargetWord().isEmpty()){
            log.info("Juego con la misma sesion{}", gameSession.getId());
            return forward(attempt, gameSession.getTargetWord(), pressedLetters, gameSession,result);
        }

        //USERS :later something like this for users relation
//        if(repository.hasUserPlayedToday(result.getUserId())) {
//            return result;
//        }

        DailyChallenge challenge = createDailyChallenge(gameSession);
        String wordDay = challenge.getWordOfTheDay().getWord();
        gameSession.setTargetWord(wordDay);
        return forward(attempt, wordDay, pressedLetters, gameSession,result);
    }

    /**
     * This method avoids duplicate code to continue with chain of responsability
     * @param attempt
     * @param target
     * @param pressedLetters
     * @param gameSession
     * @param result
     * @return a dto about dto for result
     */
    private ResultGuessDTO forward(String attempt, String target, List<PressedLetterDTO> pressedLetters, GameSession gameSession, ResultGuessDTO result) {
        if ( next != null ) {
            return next.handle(attempt, target, pressedLetters, gameSession, result);
        }
        return result;
    }

    /**
     * This method helps to create a daily challenge with DAILY mode
     * @param gameSession
     * @return the daily challenge
     */
    private DailyChallenge createDailyChallenge(GameSession gameSession) {
        if(repository.existsByGameSession(gameSession)) {
            throw new IllegalStateException("La challenge ya existe");
        }

        WordOfTheDay todayWord = getWordToday();

        DailyChallenge daily = DailyChallenge.builder()
                .gameSession(gameSession)
                .wordOfTheDay(todayWord).build();

        return repository.save(daily);
    }

    /**
     * This method returns the word of current day
     * @return Entity about the word of current day
     */
    public WordOfTheDay getWordToday() {
        return wordOfTheDayService.getTodayWord();
    }


}
