package com.example.microservicetelegram.exception;

public class ClientConflictException extends RuntimeException {

    public ClientConflictException() {
        super();
    }

    public ClientConflictException(String message) {
        super(message);
    }

}
