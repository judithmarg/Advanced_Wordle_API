package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.WordOfTheDayRequest;
import com.beproject.wordleapi.domain.dto.WordOfTheDayResponse;

import java.util.List;
import java.util.UUID;

public interface WordOfTheDayService {
    List<WordOfTheDayResponse> getAllWordsOfTheDays();
    WordOfTheDayResponse getWordOfTheDayById(UUID id);
    WordOfTheDayResponse addWordOfTheDay(WordOfTheDayRequest wordOfTheDayRequest);
    WordOfTheDayResponse updateWordOfTheDay(UUID id, WordOfTheDayRequest request);
}
