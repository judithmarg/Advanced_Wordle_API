package com.beproject.wordleapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "game_session")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 5)
    private String targetWord;

    @Column(length = 20, nullable = false)
    private String mode;

    @Column(length = 20, nullable = false)
    private String status;

    @CreationTimestamp
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @OneToOne(mappedBy = "gameSession", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DailyChallenge dailyChallenge;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventGame> eventsGame;
}