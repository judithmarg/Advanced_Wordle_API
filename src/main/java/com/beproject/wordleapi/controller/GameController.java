package com.beproject.wordleapi.controller;

import com.beproject.wordleapi.domain.dto.ResultGuessDTO;
import com.beproject.wordleapi.domain.dto.WordGuessDTO;
import com.beproject.wordleapi.service.GameSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Tag(name = "Game to guess the word", description = "Simulador de adivinar la palabra")
@SecurityRequirement(name = "bearerAuth")
public class GameController {

    private final GameSessionService service;

    /**
     * This method allows to play the guessing of the word with 6 attempts
     * @param wordGuessDTO
     * @return DTO of result of the game/attempt
     */
    @Operation(
            summary = "Adivinar la palabra del día de 5 letras",
            description = "Escribe una palabra para adivinar la propuesta. Tienes 6 intentos, asi obtienes un resultado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Datos correctos para intento"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    })
    @PostMapping("/guess")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PLAYER')")
    public ResponseEntity<ResultGuessDTO> addWordOfTheDay(@Valid @RequestBody WordGuessDTO wordGuessDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guessWord((wordGuessDTO)));
    }

}
