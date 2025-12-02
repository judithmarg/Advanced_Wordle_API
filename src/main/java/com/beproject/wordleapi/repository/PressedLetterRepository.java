package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.PressedLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PressedLetterRepository extends JpaRepository<PressedLetter, UUID> {
    @Query()
    List<PressedLetter> findByGameSessionId(UUID gameSessionId);
}
