package com.beproject.wordleapi.domain.dto;

import java.util.Set;

public record UserResponseDTO(
    Long id, 
    String username, 
    String email, 
    boolean active, 
    Set<String> roles
) {}