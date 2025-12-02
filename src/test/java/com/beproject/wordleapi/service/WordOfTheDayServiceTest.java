package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.WordOfTheDayRequest;
import com.beproject.wordleapi.domain.dto.WordOfTheDayResponse;
import com.beproject.wordleapi.domain.entity.WordOfTheDay;
import com.beproject.wordleapi.domain.exception.WordExistentException;
import com.beproject.wordleapi.mapper.WordOfTheDayMapper;
import com.beproject.wordleapi.repository.WordOfTheDayRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WordOfTheDayServiceTest {

    @Mock private WordOfTheDayRepository repository;
    @Mock private WordOfTheDayMapper mapper;

    @InjectMocks
    private WordOfTheDayServiceImpl service;

    private WordOfTheDay wordEntity;
    private WordOfTheDayResponse wordResponse;
    private WordOfTheDayRequest wordRequest;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        
        wordEntity = new WordOfTheDay();
        wordEntity.setId(uuid);
        wordEntity.setWord("CASAS");
        wordEntity.setPublishDate(LocalDate.now());

        wordResponse = new WordOfTheDayResponse(uuid, "CASAS", LocalDate.now());
        wordRequest = new WordOfTheDayRequest("CASAS");
    }

    @Test
    void getAllShouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(wordEntity));
        when(mapper.toDto(any(WordOfTheDay.class))).thenReturn(wordResponse);

        List<WordOfTheDayResponse> result = service.getAllWordsOfTheDays();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getTodayShouldReturnWord_WhenExists() {
        when(repository.findByPublishDate(any(LocalDate.class))).thenReturn(Optional.of(wordEntity));
        when(mapper.toDto(any(WordOfTheDay.class))).thenReturn(wordResponse);

        WordOfTheDayResponse result = service.getTodayWordOfTheDay();

        assertNotNull(result);
        assertEquals("CASAS", result.word());
    }

    @Test
    void getTodayShouldThrowException_WhenNotExists() {
        when(repository.findByPublishDate(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getTodayWordOfTheDay());
    }

    @Test
    void addWordShouldSave_WhenNotExists() {
        when(repository.existsByPublishDate(any())).thenReturn(false);
        when(mapper.toEntity(any(WordOfTheDayRequest.class))).thenReturn(wordEntity);
        
        when(repository.save(any(WordOfTheDay.class))).thenReturn(wordEntity); 
        when(mapper.toDto(any(WordOfTheDay.class))).thenReturn(wordResponse);

        WordOfTheDayResponse result = service.addWordOfTheDay(wordRequest);

        assertNotNull(result);
        
        verify(repository).save(any(WordOfTheDay.class)); 
    }

    @Test
    void addWordShouldThrowException_WhenAlreadyExists() {
        when(repository.existsByPublishDate(any())).thenReturn(true);

        assertThrows(WordExistentException.class, () -> service.addWordOfTheDay(wordRequest));
        
        verify(repository, never()).save(any());
    }
}