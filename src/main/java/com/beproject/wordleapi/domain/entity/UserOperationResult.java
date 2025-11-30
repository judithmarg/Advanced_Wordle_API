package com.beproject.wordleapi.domain.entity;

public sealed interface UserOperationResult permits UserOperationResult.Success, UserOperationResult.Failure {
    
    final class Success implements UserOperationResult {
        private final User user;
        public Success(User user) { this.user = user; }
        public User getUser() { return user; }
    }

    final class Failure implements UserOperationResult {
        private final String message;
        public Failure(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}