package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.DailyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DailyChallengeRepository extends JpaRepository<DailyChallenge, UUID> {

    @Query("SELECT d FROM DailyChallenge d WHERE d.gameSessionId = : gameId")
    void saveDailyPlay(@Param("gameId") UUID gameId);
}
