package com.beproject.wordleapi.controller;

import com.beproject.wordleapi.domain.dto.WordOfTheDayRequest;
import com.beproject.wordleapi.domain.dto.WordOfTheDayResponse;
import com.beproject.wordleapi.service.WordOfTheDayServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/word-of-the-day")
@RequiredArgsConstructor
public class WordOfTheDayController {

    private final WordOfTheDayServiceImpl service;

    @GetMapping
    public ResponseEntity<List<WordOfTheDayResponse>> getAllWordsOfTheDays() {
        return ResponseEntity.ok(service.getAllWordsOfTheDays());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordOfTheDayResponse> getWordOfTheDay(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getWordOfTheDayById(id));
    }

    @PostMapping
    public ResponseEntity<WordOfTheDayResponse> addWordOfTheDay(@Valid @RequestBody WordOfTheDayRequest wordOfTheDayRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addWordOfTheDay((wordOfTheDayRequest)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WordOfTheDayResponse> modifyWordOfTheDay(@PathVariable UUID id, @Valid @RequestBody WordOfTheDayRequest request) {
        return ResponseEntity.ok(service.updateWordOfTheDay(id, request));
    }
}
