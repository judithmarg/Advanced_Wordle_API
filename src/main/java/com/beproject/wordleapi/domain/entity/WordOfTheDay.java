package com.beproject.wordleapi.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(
        name= "word_of_the_day",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "publishDate")
        })
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class WordOfTheDay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String word;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate publishDate;

    @OneToMany(mappedBy = "wordOfTheDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyChallenge> dailyChallenges = new ArrayList<>();
}