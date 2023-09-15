package com.parking.exception;

public class UsernameUniqueViolationException extends RuntimeException {
    
    public UsernameUniqueViolationException(String message) {
        super(message);
    }
}
