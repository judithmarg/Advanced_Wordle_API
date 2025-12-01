package com.beproject.wordleapi.controller;

import com.beproject.wordleapi.domain.dto.WordOfTheDayRequest;
import com.beproject.wordleapi.domain.dto.WordOfTheDayResponse;
import com.beproject.wordleapi.service.WordOfTheDayServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Word of the day", description = "Gestión de la palabra del dia")
public class WordOfTheDayController {

    private final WordOfTheDayServiceImpl service;

    /**
     * This method return all words established
     * @return List of DTOs of words of the days
     */
    @Operation(
            summary = "Obtener todas las palabras del día",
            description = "Devuelve una lista con todas las palabras registradas como Word of the Day."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<WordOfTheDayResponse>> getAllWordsOfTheDays() {
        return ResponseEntity.ok(service.getAllWordsOfTheDays());
    }

    /**
     * This method return the word for today
     * @return DTO of word of the day
     */
    @Operation(
            summary = "Obtener la palabra del día de hoy",
            description = "Devuelve el WordOfTheDay correspondiente a la fecha actual."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Palabra del día obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe palabra asignada para hoy")
    })
    @GetMapping("/today")
    public ResponseEntity<WordOfTheDayResponse> getTodayWordOfTheDay() {
        return ResponseEntity.ok(service.getTodayWordOfTheDay());
    }

    /**
     * This method return the word of the day given an id
     * @param id
     * @return DTO of word of the day with that id
     */
    @Operation(
            summary = "Obtener una palabra del día por ID",
            description = "Devuelve el WordOfTheDay correspondiente al ID proporcionado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Palabra encontrada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe una palabra con ese id")
    })
    @GetMapping("/{id}")
    public ResponseEntity<WordOfTheDayResponse> getWordOfTheDay(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getWordOfTheDayById(id));
    }

    /**
     * This method create the word of today, if it exists, throws an error
     * @param wordOfTheDayRequest
     * @return DTO of word of the day already created
     */
    @Operation(
            summary = "Crear la palabra del día de 5 letras",
            description = "Registra una nueva palabra del día. Si ya existe una palabra para la fecha actual, devuelve error."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Palabra creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "409", description = "Ya existe una palabra para el día de hoy")
    })
    @PostMapping
    public ResponseEntity<WordOfTheDayResponse> addWordOfTheDay(@Valid @RequestBody WordOfTheDayRequest wordOfTheDayRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addWordOfTheDay((wordOfTheDayRequest)));
    }

    /**
     * This method update the word of a day, if its id does not exist, throws an error
     * @param id
     * @param request
     * @return DTO of word of the day already updated
     */
    @Operation(
            summary = "Modificar la palabra del día",
            description = "Actualiza una palabra del día existente según su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Palabra actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "No existe una palabra con ese ID")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<WordOfTheDayResponse> modifyWordOfTheDay(@PathVariable UUID id, @Valid @RequestBody WordOfTheDayRequest request) {
        return ResponseEntity.ok(service.updateWordOfTheDay(id, request));
    }
}
