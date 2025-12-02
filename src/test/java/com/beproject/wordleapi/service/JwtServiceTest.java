package com.beproject.wordleapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        
        userDetails = new User("lucia", "password", Collections.emptyList());
    }

    @Test
    void generateToken_ShouldReturnToken() {
        String token = jwtService.generateToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        
        String username = jwtService.extractUsername(token);
        
        assertEquals("lucia", username);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        String token = jwtService.generateToken(userDetails);
        
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForDifferentUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("judith", "password", Collections.emptyList());
        
        boolean isValid = jwtService.isTokenValid(token, otherUser);
        
        assertFalse(isValid);
    }
}