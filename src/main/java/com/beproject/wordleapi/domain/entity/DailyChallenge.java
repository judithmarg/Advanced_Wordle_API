package com.beproject.wordleapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "daily_challenge",
        uniqueConstraints = @UniqueConstraint(columnNames = "game_session_id"
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyChallenge {

    @Id
    private UUID gameSessionId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "word_of_the_day_id", nullable = false)
    private WordOfTheDay wordOfTheDay;
}
