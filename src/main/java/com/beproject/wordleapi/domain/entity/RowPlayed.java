package com.beproject.wordleapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "row_played")
@Getter
@Setter
public class RowPlayed extends EventGame{

    private int rowNumber;

    @Column(length = 5)
    private String wordContent;

    @Column(length = 5)
    private String resultPattern;
}