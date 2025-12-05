package com.beproject.wordleapi.controller;

import com.beproject.wordleapi.domain.dto.WordOfTheDayRequest;
import com.beproject.wordleapi.domain.dto.WordOfTheDayResponse;
import com.beproject.wordleapi.service.WordOfTheDayService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WordOfTheDayControllerTest {

    @Mock
    private WordOfTheDayService service;

    @InjectMocks
    private WordOfTheDayController controller;

    private WordOfTheDayResponse wordResponse;
    private WordOfTheDayRequest wordRequest;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        wordResponse = new WordOfTheDayResponse(uuid, "nuevo", LocalDate.now());
        wordRequest = new WordOfTheDayRequest("NUEVO");
    }

    @Test
    void shouldGetAllReturnListEmpty() {
        when(service.getAllWordsOfTheDays()).thenReturn(Collections.emptyList());
        ResponseEntity<List<WordOfTheDayResponse>> result = controller.getAllWordsOfTheDays();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(Collections.emptyList(),result.getBody());
    }

    @Test
    void shouldGetAllReturnListHasElements() {
        when(service.getAllWordsOfTheDays()).thenReturn(List.of(wordResponse));
        ResponseEntity<List<WordOfTheDayResponse>> result = controller.getAllWordsOfTheDays();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(wordResponse,result.getBody().get(0));
    }

    @Test
    void shouldGetTodayWord(){
        when(service.getTodayWordOfTheDay()).thenReturn(wordResponse);
        ResponseEntity<WordOfTheDayResponse> result = controller.getTodayWordOfTheDay();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(wordResponse,result.getBody());
    }

    @Test
    void tryGetTodayWord(){
        when(service.getTodayWordOfTheDay()).thenThrow(new EntityNotFoundException("Word of the day was not defined."));
        assertThrows(EntityNotFoundException.class,
                () -> controller.getTodayWordOfTheDay());
    }

    @Test
    void shouldGetWordById() {
        when(service.getWordOfTheDayById(uuid)).thenReturn(wordResponse);

        ResponseEntity<WordOfTheDayResponse> result = controller.getWordOfTheDay(uuid);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(wordResponse, result.getBody());
    }

    @Test
    void tryGetWordByIdNotFound() {
        when(service.getWordOfTheDayById(uuid))
                .thenThrow(new EntityNotFoundException("Not found"));

        assertThrows(EntityNotFoundException.class,
                () -> controller.getWordOfTheDay(uuid));
    }

    @Test
    void shouldCreateWord() {
        when(service.addWordOfTheDay(wordRequest)).thenReturn(wordResponse);

        ResponseEntity<WordOfTheDayResponse> result =
                controller.addWordOfTheDay(wordRequest);

        assertEquals(201, result.getStatusCode().value());
        assertEquals(wordResponse, result.getBody());
    }

    @Test
    void shouldThrowWhenCreateWordAlreadyExists() {
        when(service.addWordOfTheDay(wordRequest))
                .thenThrow(new IllegalStateException("Already exists"));

        assertThrows(IllegalStateException.class,
                () -> controller.addWordOfTheDay(wordRequest));
    }


    @Test
    void shouldUpdateWord() {
        when(service.updateWordOfTheDay(uuid, wordRequest)).thenReturn(wordResponse);

        ResponseEntity<WordOfTheDayResponse> result =
                controller.modifyWordOfTheDay(uuid, wordRequest);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(wordResponse, result.getBody());
    }


}
