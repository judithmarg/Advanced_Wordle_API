package com.beproject.wordleapi.domain.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(Map.of("error", resolveMessage(ex.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(Map.of("error", "Usuario o contraseña incorrectos"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(WordExistentException.class)
    public ResponseEntity<Map<String, String>> handleWordExistent(WordExistentException ex) {
        String translatedMessage = resolveMessage(ex.getMessage());
        return new ResponseEntity<>(Map.of("error", translatedMessage), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(jakarta.persistence.EntityNotFoundException ex) {
        return new ResponseEntity<>(Map.of("error", resolveMessage(ex.getMessage())), HttpStatus.NOT_FOUND);
    }

    private String resolveMessage(String key) {
        if (key == null) return "Unknown error";

        String cleanKey = key.replace("{", "").replace("}", "");

        try {
            return messageSource.getMessage(cleanKey, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return cleanKey;
        }
    }
}