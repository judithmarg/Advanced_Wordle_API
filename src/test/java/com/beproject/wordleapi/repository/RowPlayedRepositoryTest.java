package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.domain.entity.RowPlayed;
import com.beproject.wordleapi.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") 
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
class RowPlayedRepositoryTest {

    @Autowired
    private RowPlayedRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void countByGameSessionShouldReturnCorrectCountWhenRowsExist() {
        User user = new User();
        user.setUsername("player1");
        user.setEmail("p@test.com");
        user.setPassword("pass");
        user.setActive(true);
        entityManager.persist(user);

        GameSession session = new GameSession();
        session.setMode("DAILY");
        session.setStatus("IN_PROGRESS");
        session.setTargetWord("APPLE");
        session.setUser(user);
        session = entityManager.persist(session);

        RowPlayed row1 = new RowPlayed();
        row1.setGameSession(session);
        row1.setRowNumber(1);
        row1.setWordContent("BERRY");
        row1.setResultPattern("WWWWW");
        entityManager.persist(row1);

        entityManager.flush();
        entityManager.clear();

        int count = repository.countByGameSession(session.getId());

        assertThat(count).isEqualTo(1);
    }
}