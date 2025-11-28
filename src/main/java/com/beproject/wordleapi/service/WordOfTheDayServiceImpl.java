package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.WordOfTheDayRequest;
import com.beproject.wordleapi.domain.dto.WordOfTheDayResponse;
import com.beproject.wordleapi.domain.entity.WordOfTheDay;
import com.beproject.wordleapi.mapper.WordOfTheDayMapper;
import com.beproject.wordleapi.repository.WordOfTheDayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WordOfTheDayServiceImpl implements WordOfTheDayService {

    private final WordOfTheDayRepository repository;
    private final WordOfTheDayMapper mapper;

    @Override
    public List<WordOfTheDayResponse> getAllWordsOfTheDays() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public WordOfTheDayResponse getWordOfTheDayById(UUID id) {
        return repository.findById(id).map(mapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public WordOfTheDayResponse addWordOfTheDay(WordOfTheDayRequest wordOfTheDayRequest) {
        WordOfTheDay wordOfTheDay = mapper.toEntity(wordOfTheDayRequest);
        wordOfTheDay = repository.save(wordOfTheDay);
        return mapper.toDto(wordOfTheDay);
    }

    @Override
    @Transactional
    public WordOfTheDayResponse updateWordOfTheDay(UUID id, WordOfTheDayRequest request) {
        WordOfTheDay wordOfTheDay = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Word of the day not found with id: "+ id));
        mapper.updateEntityFromDto(request, wordOfTheDay);
        WordOfTheDay updatedWord = repository.save(wordOfTheDay);
        return mapper.toDto(wordOfTheDay);
    }

}
