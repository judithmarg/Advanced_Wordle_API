package com.beproject.wordleapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pressed_letter", uniqueConstraints = @UniqueConstraint(columnNames = {"game_session_id", "letter"}))
@NamedQuery(
        name = "PressedLetter.findByGameSessionId",
        query = "SELECT p FROM PressedLetter p WHERE p.gameSession.id = :gameSessionId"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PressedLetter extends EventGame{

    @Column(length = 1, nullable = false)
    private char letter;

    @Column(length = 20)
    private String status;

}
