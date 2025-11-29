package com.beproject.wordleapi.domain.exception;

public class WordExistentException extends RuntimeException{
    public WordExistentException(String message){
        super(message);
    }
}
