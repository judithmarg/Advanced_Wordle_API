package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.DailyChallenge;
import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.domain.entity.User;
import com.beproject.wordleapi.domain.entity.WordOfTheDay;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DailyChallengeRepositoryTest {

    @Autowired
    private DailyChallengeRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByDailyPlayShouldReturnChallengeWhenSessionIdMatches() {

        User user = new User();
        user.setUsername("dailyPlayer");
        user.setEmail("daily@test.com");
        user.setPassword("password");
        user.setActive(true);
        entityManager.persist(user);

        GameSession session = new GameSession();
        session.setUser(user);
        session.setMode("DAILY");
        session.setStatus("IN_PROGRESS");
        session.setTargetWord("WORLD");
        session = entityManager.persist(session);

        WordOfTheDay wordOfTheDay = new WordOfTheDay();
        wordOfTheDay.setWord("WORLD");
        wordOfTheDay.setPublishDate(LocalDate.now());
        entityManager.persist(wordOfTheDay);

        DailyChallenge challenge = new DailyChallenge();
        challenge.setGameSession(session);
        challenge.setWordOfTheDay(wordOfTheDay);
        entityManager.persist(challenge);

        entityManager.flush();
        entityManager.clear();

        DailyChallenge found = repository.findByDailyPlay(session.getId());

        assertThat(found).isNotNull();
        assertThat(found.getGameSession().getId()).isEqualTo(session.getId());
    }

    @Test
    void findByDailyPlayShouldReturnNullWhenSessionIdDoesNotExist() {

        UUID randomId = UUID.randomUUID();

        DailyChallenge found = repository.findByDailyPlay(randomId);

        assertThat(found).isNull();
    }

    @Test
    void existsByGameSessionShouldReturnTrueWhenChallengeExists() {
        User user = new User();
        user.setUsername("existsPlayer");
        user.setEmail("exists@test.com");
        user.setPassword("pass");
        user.setActive(true);
        entityManager.persist(user);

        GameSession session = new GameSession();
        session.setUser(user);
        session.setMode("DAILY");
        session.setStatus("WON");
        session = entityManager.persist(session);

        WordOfTheDay wordOfTheDay = new WordOfTheDay();
        wordOfTheDay.setWord("EXIST");
        wordOfTheDay.setPublishDate(LocalDate.now().plusDays(1));
        entityManager.persist(wordOfTheDay);

        DailyChallenge challenge = new DailyChallenge();
        challenge.setGameSession(session);
        challenge.setWordOfTheDay(wordOfTheDay);
        entityManager.persist(challenge);

        entityManager.flush();

        boolean exists = repository.existsByGameSession(session);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByGameSessionShouldReturnFalseWhenChallengeDoesNotExist() {

        User user = new User();
        user.setUsername("noChallengePlayer");
        user.setEmail("no@test.com");
        user.setPassword("pass");
        user.setActive(true);
        entityManager.persist(user);

        GameSession session = new GameSession();
        session.setUser(user);
        session.setMode("DAILY");
        session.setStatus("IN_PROGRESS");
        entityManager.persist(session);

        entityManager.flush();

        boolean exists = repository.existsByGameSession(session);

        assertThat(exists).isFalse();
    }
}