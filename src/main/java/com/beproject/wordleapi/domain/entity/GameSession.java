package com.beproject.wordleapi.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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

    @NotNull
    private String targetWord;

    private String mode;

    private String status;

    @CreationTimestamp
    private LocalDateTime startAt;

    private LocalDateTime completedAt;
}
