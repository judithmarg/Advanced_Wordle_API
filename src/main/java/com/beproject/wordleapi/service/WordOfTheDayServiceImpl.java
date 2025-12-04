package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.WordOfTheDayRequest;
import com.beproject.wordleapi.domain.dto.WordOfTheDayResponse;
import com.beproject.wordleapi.domain.entity.WordOfTheDay;
import com.beproject.wordleapi.domain.exception.WordExistentException;
import com.beproject.wordleapi.mapper.WordOfTheDayMapper;
import com.beproject.wordleapi.repository.WordOfTheDayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordOfTheDayServiceImpl implements WordOfTheDayService {

    private final WordOfTheDayRepository repository;
    private final WordOfTheDayMapper mapper;

    /**
     * This method return all words established
     * @return List of DTOs of words of the days
     */
    @Override
    @Transactional(readOnly = true)
    public List<WordOfTheDayResponse> getAllWordsOfTheDays() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * This method return the word for today
     * @return DTO of word of the day
     */
    @Override
    @Transactional(readOnly = true)
    public WordOfTheDayResponse getTodayWordOfTheDay() {
        LocalDate today = LocalDate.now();
        WordOfTheDay word = repository.findByPublishDate(today)
                .orElseThrow(() -> new EntityNotFoundException("Word of the day was not defined."));
        return mapper.toDto(word);
    }

    /**
     * This method return the word of the day given an id
     * @param id
     * @return DTO of word of the day with that id
     */
    @Override
    @Transactional(readOnly = true)
    public WordOfTheDayResponse getWordOfTheDayById(UUID id) {
        return repository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Word of the day not found with id: "+ id));
    }

    /**
     * This method create the word of today, if it exists, throws an error
     * @param wordOfTheDayRequest
     * @return DTO of word of the day already created
     */
    @Override
    @Transactional
    public WordOfTheDayResponse addWordOfTheDay(WordOfTheDayRequest wordOfTheDayRequest) {
        LocalDate today = LocalDate.now();
        if(repository.existsByPublishDate(today)){
            throw new WordExistentException("{word.existent}");
        }
        WordOfTheDay wordOfTheDay = mapper.toEntity(wordOfTheDayRequest);
        wordOfTheDay.setWord(wordOfTheDay.getWord().toUpperCase());
        return mapper.toDto(repository.save(wordOfTheDay)); 
    }

    /**
     * This method update the word of a day, if its id does not exist, throws an error
     * @param id
     * @param request
     * @return DTO of word of the day already updated
     */
    @Override
    @Transactional
    public WordOfTheDayResponse updateWordOfTheDay(UUID id, WordOfTheDayRequest request) {
        WordOfTheDay wordOfTheDay = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Word of the day not found with id: "+ id));
        mapper.updateEntityFromDto(request, wordOfTheDay);
        wordOfTheDay.setWord(wordOfTheDay.getWord().toUpperCase());
        WordOfTheDay updatedWord = repository.save(wordOfTheDay);
        return mapper.toDto(updatedWord);
    }

    @Override
    public WordOfTheDay getTodayWord() {
        LocalDate today = LocalDate.now();
        return repository.findByPublishDate(today)
                .orElseThrow(() -> new EntityNotFoundException("Word of the day was not defined."));
    }


}
