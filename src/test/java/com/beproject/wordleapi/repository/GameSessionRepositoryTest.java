package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GameSessionRepositoryTest {

    @Autowired
    private GameSessionRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindLastByModeAndStatusOrderedByStartedAt() {
        User user = new User();
        user.setUsername("admin");
        user.setEmail("a@test.com");
        user.setPassword("pass");
        user.setActive(true);
        entityManager.persist(user);

        GameSession s1 = new GameSession();
        s1.setMode("DAILY");
        s1.setStatus("IN_PROGRESS");
        s1.setUser(user);
        s1.setTargetWord("APPLE");
        s1.setStartedAt(LocalDateTime.now().minusHours(5));
        entityManager.persist(s1);

        GameSession s2 = new GameSession();
        s2.setMode("DAILY");
        s2.setStatus("IN_PROGRESS");
        s2.setUser(user);
        s2.setTargetWord("BERRY");
        s2.setStartedAt(LocalDateTime.now().minusHours(1));
        entityManager.persist(s2);

        entityManager.flush();
        entityManager.clear();

        List<GameSession> result =
                repository.findLastByModeAndStatus(user.getId(), "DAILY", "IN_PROGRESS");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStartedAt())
                .isAfter(result.get(1).getStartedAt());
        assertThat(result.get(0).getTargetWord()).isEqualTo("BERRY");
        assertThat(result.get(1).getTargetWord()).isEqualTo("APPLE");
    }

    @Test
    void shouldHasUserFinishedDailyGameToday() {
        User user = new User();
        user.setUsername("player");
        user.setEmail("p@test.com");
        user.setPassword("pass");
        user.setActive(true);
        entityManager.persist(user);

        GameSession session = new GameSession();
        session.setMode("DAILY");
        session.setStatus("WIN");
        session.setUser(user);
        session.setTargetWord("APPLE");
        session.setStartedAt(LocalDateTime.now().minusHours(2));
        entityManager.persist(session);

        entityManager.flush();
        entityManager.clear();

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        boolean result = repository.hasUserFinishedDailyGameToday(
                user.getId(), startOfDay, endOfDay
        );

        assertThat(result).isTrue();
    }

    @Test
    void shouldHasUserFinishedDailyGameWhenNoGamesFinishedToday() {
        User user = new User();
        user.setUsername("player2");
        user.setEmail("p2@test.com");
        user.setPassword("pass");
        user.setActive(true);
        entityManager.persist(user);

        GameSession session = new GameSession();
        session.setMode("DAILY");
        session.setStatus("IN_PROGRESS");
        session.setUser(user);
        session.setTargetWord("BERRY");
        session.setStartedAt(LocalDateTime.now().minusDays(1));
        entityManager.persist(session);

        entityManager.flush();
        entityManager.clear();

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        boolean result = repository.hasUserFinishedDailyGameToday(
                user.getId(), startOfDay, endOfDay
        );

        assertThat(result).isFalse();
    }
}
