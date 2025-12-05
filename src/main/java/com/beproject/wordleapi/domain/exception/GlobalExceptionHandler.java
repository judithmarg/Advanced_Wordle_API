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

    // 1. Inyectamos el componente encargado de leer messages.properties
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
        // También aplicamos la traducción aquí por si acaso usas claves en IllegalArgumentException
        return new ResponseEntity<>(Map.of("error", resolveMessage(ex.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(Map.of("error", "Usuario o contraseña incorrectos"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(WordExistentException.class)
    public ResponseEntity<Map<String, String>> handleWordExistent(WordExistentException ex) {
        // 2. Usamos el método auxiliar para traducir la clave "{word.existent}"
        String translatedMessage = resolveMessage(ex.getMessage());
        return new ResponseEntity<>(Map.of("error", translatedMessage), HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(jakarta.persistence.EntityNotFoundException ex) {
        return new ResponseEntity<>(Map.of("error", resolveMessage(ex.getMessage())), HttpStatus.NOT_FOUND);
    }

    // --- MÉTODO AUXILIAR PARA TRADUCIR ---
    private String resolveMessage(String key) {
        if (key == null) return "Unknown error";
        
        // Limpiamos las llaves si vienen en el formato "{clave}"
        String cleanKey = key.replace("{", "").replace("}", "");

        try {
            // Buscamos la traducción en messages.properties usando el idioma actual
            return messageSource.getMessage(cleanKey, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            // Si no encuentra la traducción, devolvemos la clave original (fallback)
            return cleanKey;
        }
    }
}