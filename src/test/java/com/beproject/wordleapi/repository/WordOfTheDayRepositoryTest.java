package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.WordOfTheDay;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WordOfTheDayRepositoryTest {

    @Autowired
    private WordOfTheDayRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldExistsByPublishDateWhenWordExists() {
        LocalDate today = LocalDate.now();

        WordOfTheDay word = new WordOfTheDay();
        word.setWord("APPLE");
        word.setPublishDate(today);

        entityManager.persist(word);
        entityManager.flush();
        entityManager.clear();

        boolean exists = repository.existsByPublishDate(today);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldExistsByPublishDateWhenWordDoesNotExist() {
        boolean exists = repository.existsByPublishDate(LocalDate.now().plusDays(1));

        assertThat(exists).isFalse();
    }

    @Test
    void shouldFindByPublishDate() {
        Optional<WordOfTheDay> result = repository.findByPublishDate(LocalDate.of(2030, 1, 1));

        assertThat(result).isEmpty();
    }
}
