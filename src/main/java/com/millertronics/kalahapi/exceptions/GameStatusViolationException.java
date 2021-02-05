package com.millertronics.kalahapi.exceptions;

public class GameStatusViolationException extends RuntimeException {
    public GameStatusViolationException(String message) {
        super(message);
    }
}
