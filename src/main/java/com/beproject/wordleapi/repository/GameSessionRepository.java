package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {

    @Query("SELECT g FROM GameSession g WHERE g.user.id = :userId AND g.mode = :mode AND g.status = :status ORDER BY g.startedAt DESC")
    List<GameSession> findLastByModeAndStatus(
            @Param("userId") Long userId, 
            @Param("mode") String mode, 
            @Param("status") String status
    );

    @Query("""
        SELECT COUNT(g) > 0 FROM GameSession g 
        WHERE g.user.id = :userId 
        AND g.mode = 'DAILY' 
        AND (g.status = 'WIN' OR g.status = 'LOST') 
        AND g.startedAt BETWEEN :startOfDay AND :endOfDay
    """)
    boolean hasUserFinishedDailyGameToday(
            @Param("userId") Long userId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
