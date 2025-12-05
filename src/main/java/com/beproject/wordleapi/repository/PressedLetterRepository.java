package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.PressedLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PressedLetterRepository extends JpaRepository<PressedLetter, UUID> {
    
    List<PressedLetter> findByGameSessionId(UUID gameSessionId);

    Optional<PressedLetter> findByGameSessionIdAndLetter(UUID gameSessionId, char letter);
}