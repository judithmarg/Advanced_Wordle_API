package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.PressedLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PressedLetterRepository extends JpaRepository<PressedLetter, UUID> {
    @Query(value = "SELECT p FROM PressedLetter p WHERE p.gameSession.id = :gameSessionId", nativeQuery = true)
    List<PressedLetter> findByGameSessionId(UUID gameSessionId);
}
