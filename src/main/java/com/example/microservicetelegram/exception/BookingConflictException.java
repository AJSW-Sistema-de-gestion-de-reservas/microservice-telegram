package com.example.microservicetelegram.exception;

public class BookingConflictException extends RuntimeException {

    public BookingConflictException() {
        super();
    }

    public BookingConflictException(String message) {
        super(message);
    }

}
