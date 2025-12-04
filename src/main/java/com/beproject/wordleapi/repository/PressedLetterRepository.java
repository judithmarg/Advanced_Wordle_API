package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.PressedLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PressedLetterRepository extends JpaRepository<PressedLetter, UUID> {

    List<PressedLetter> findByGameSessionId(@Param("gameSessionId") UUID gameSessionId);

    Optional<PressedLetter> findByGameSessionIdAndLetter(UUID id, char letter);
}
