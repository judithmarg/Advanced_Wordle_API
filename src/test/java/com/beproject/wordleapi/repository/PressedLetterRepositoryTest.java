package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.GameSession;
import com.beproject.wordleapi.domain.entity.PressedLetter;
import com.beproject.wordleapi.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource; 

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.sql.init.mode=never",
    
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PressedLetterRepositoryTest {

    @Autowired
    private PressedLetterRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByGameSessionIdShouldReturnListOfLettersWhenTheyExist() {
        User user = new User();
        user.setUsername("letterTester");
        user.setEmail("l@test.com");
        user.setPassword("pass");
        user.setActive(true);
        entityManager.persist(user);

        GameSession session = new GameSession();
        session.setMode("RANDOM");
        session.setStatus("IN_PROGRESS");
        session.setUser(user);
        entityManager.persist(session);

        PressedLetter letterA = new PressedLetter();
        letterA.setGameSession(session);
        letterA.setLetter('A');
        letterA.setStatus("CORRECT");
        entityManager.persist(letterA);

        PressedLetter letterB = new PressedLetter();
        letterB.setGameSession(session);
        letterB.setLetter('B');
        letterB.setStatus("WRONG");
        entityManager.persist(letterB);

        entityManager.flush();

        List<PressedLetter> results = repository.findByGameSessionId(session.getId());

        assertThat(results).hasSize(2);
        assertThat(results).extracting(PressedLetter::getLetter).contains('A', 'B');
    }

    @Test
    void findByGameSessionIdShouldReturnEmptyListWhenNoLettersSaved() {
        User user = new User();
        user.setUsername("emptyTester");
        user.setEmail("e@test.com");
        user.setPassword("pass");
        user.setActive(true);
        entityManager.persist(user);

        GameSession session = new GameSession();
        session.setMode("RANDOM");
        session.setStatus("IN_PROGRESS");
        session.setUser(user);
        entityManager.persist(session);
        
        entityManager.flush();

        List<PressedLetter> results = repository.findByGameSessionId(session.getId());

        assertThat(results).isEmpty();
    }

    @Test
    void findByGameSessionIdAndLetterShouldReturnLetterWhenExists() {
        User user = new User();
        user.setUsername("finder");
        user.setEmail("f@test.com");
        user.setPassword("pass");
        user.setActive(true);
        entityManager.persist(user);

        GameSession session = new GameSession();
        session.setMode("DAILY");
        session.setStatus("IN_PROGRESS");
        session.setUser(user);
        entityManager.persist(session);

        PressedLetter letterZ = new PressedLetter();
        letterZ.setGameSession(session);
        letterZ.setLetter('Z');
        letterZ.setStatus("MISPLACED");
        entityManager.persist(letterZ);

        entityManager.flush();

        Optional<PressedLetter> result = repository.findByGameSessionIdAndLetter(session.getId(), 'Z');

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo("MISPLACED");
    }

    @Test
    void findByGameSessionIdAndLetterShouldReturnEmptyWhenLetterNotExists() {
        User user = new User();
        user.setUsername("seeker");
        user.setEmail("s@test.com");
        user.setPassword("pass");
        user.setActive(true);
        entityManager.persist(user);

        GameSession session = new GameSession();
        session.setMode("DAILY");
        session.setStatus("IN_PROGRESS");
        session.setUser(user);
        entityManager.persist(session);

        PressedLetter letterX = new PressedLetter();
        letterX.setGameSession(session);
        letterX.setLetter('X');
        letterX.setStatus("WRONG");
        entityManager.persist(letterX);

        entityManager.flush();

        Optional<PressedLetter> result = repository.findByGameSessionIdAndLetter(session.getId(), 'Y');

        assertThat(result).isEmpty();
    }
}