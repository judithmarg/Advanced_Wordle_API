package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {
}
