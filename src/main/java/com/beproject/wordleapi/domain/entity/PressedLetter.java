package com.beproject.wordleapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pressed_letter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PressedLetter extends EventGame {

    @Column(length = 1, nullable = false)
    private char letter;

    @Column(length = 20)
    private String status;
}