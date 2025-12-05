package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") 
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUsernameShouldReturnUserWhenExists() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@email.com");
        user.setPassword("secretPass");
        user.setActive(true);
        
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> result = repository.findByUsername("testUser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testUser");
        assertThat(result.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void findByUsernameShouldReturnEmptyWhenUserDoesNotExist() {
        Optional<User> result = repository.findByUsername("ghostUser");

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmailShouldReturnUserWhenExists() {
        User user = new User();
        user.setUsername("mailUser");
        user.setEmail("findme@email.com");
        user.setPassword("pass");
        user.setActive(true);
        
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> result = repository.findByEmail("findme@email.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("mailUser");
    }

    @Test
    void existsByUsernameShouldReturnTrueWhenUserExists() {
        User user = new User();
        user.setUsername("existCheck");
        user.setEmail("check@test.com");
        user.setPassword("pass");
        user.setActive(true);
        
        entityManager.persist(user);
        entityManager.flush();

        boolean exists = repository.existsByUsername("existCheck");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsernameShouldReturnFalseWhenUserDoesNotExist() {
        boolean exists = repository.existsByUsername("nonExistentUser");

        assertThat(exists).isFalse();
    }
}