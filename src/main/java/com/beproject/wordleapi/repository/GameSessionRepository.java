package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {

    @Query("SELECT g FROM GameSession g WHERE g.mode = :mode AND g.status = :status ORDER BY g.startedAt DESC")
    List<GameSession> findLastByModeAndStatus(@Param("mode") String mode, @Param("status") String status);
}
