package com.beproject.wordleapi.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(
    @NotBlank(message = "El usuario es requerido") String username,
    @NotBlank(message = "La contraseña es requerida") String password
) {}