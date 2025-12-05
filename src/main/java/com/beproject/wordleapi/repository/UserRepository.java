package com.beproject.wordleapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beproject.wordleapi.domain.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
}
