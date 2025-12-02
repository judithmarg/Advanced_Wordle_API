package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.RowPlayed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RowPlayedRepository extends JpaRepository<RowPlayed, UUID> {

    @Query(value = "SELECT COUNT(*) FROM row_played r WHERE r.game_session_id = :gameSessionId", nativeQuery = true)
    int countByGameSession(@Param("gameSessionId") UUID gameSessionId);
}
