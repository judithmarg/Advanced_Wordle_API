package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.RowPlayed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RowPlayedRepository extends JpaRepository<RowPlayed, UUID> {

    @Query("SELECT COUNT(r) FROM RowPlayed r WHERE r.gameSession.id = :gameSessionId")
    int countByGameSession(@Param("gameSessionId") UUID gameSessionId);
}
