package com.example.microservicetelegram.exception;

public class AccommodationConflictException extends RuntimeException {

    public AccommodationConflictException() {
        super();
    }

    public AccommodationConflictException(String message) {
        super(message);
    }
    
}
