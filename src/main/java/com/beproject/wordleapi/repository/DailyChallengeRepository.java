package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.DailyChallenge;
import com.beproject.wordleapi.domain.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DailyChallengeRepository extends JpaRepository<DailyChallenge, UUID> {

    @Query("SELECT d FROM DailyChallenge d WHERE d.gameSessionId = :gameId")
    DailyChallenge findByDailyPlay(@Param("gameId") UUID gameId);

    boolean existsByGameSession(GameSession gameSession);
}


