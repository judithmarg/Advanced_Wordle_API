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

    @Override
    public ResultGuessDTO handle(String attempt, String target, List<PressedLetterDTO> pressedLetters, GameSession gameSession, ResultGuessDTO result) {

        //when the random mode is selected
        if(gameSession.getTargetWord() != null &&  !gameSession.getTargetWord().isEmpty()){
            log.info("por game session {}", gameSession.getTargetWord());
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

    private ResultGuessDTO forward(String attempt, String target, List<PressedLetterDTO> pressedLetters, GameSession gameSession, ResultGuessDTO result) {
        if ( next != null ) {
            return next.handle(attempt, target, pressedLetters, gameSession, result);
        }
        return result;
    }

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

    public WordOfTheDay getWordToday() {
        return wordOfTheDayService.getTodayWord();
    }


}
