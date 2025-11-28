package com.beproject.wordleapi.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name= "word_of_the_day",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "publishDate")
        })
@Getter
@Setter
@RequiredArgsConstructor
public class WordOfTheDay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String word;

    @CreationTimestamp
    @Column(nullable = false, unique = true)
    private LocalDateTime publishDate;
}
