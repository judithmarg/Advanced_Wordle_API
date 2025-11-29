package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.WordOfTheDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface WordOfTheDayRepository extends JpaRepository<WordOfTheDay, UUID> {

    boolean existsByPublishDate(LocalDate publishDate);

}
