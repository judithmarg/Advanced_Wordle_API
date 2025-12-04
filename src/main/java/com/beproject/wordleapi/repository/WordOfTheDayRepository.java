package com.beproject.wordleapi.repository;

import com.beproject.wordleapi.domain.entity.WordOfTheDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WordOfTheDayRepository extends JpaRepository<WordOfTheDay, UUID> {

    boolean existsByPublishDate(LocalDate publishDate);

    @Query(value = "SELECT * FROM word_of_the_day w WHERE w.publish_date = :publishDate", nativeQuery = true)
    Optional<WordOfTheDay> findByPublishDate(@Param("publishDate") LocalDate publishDate);

}
