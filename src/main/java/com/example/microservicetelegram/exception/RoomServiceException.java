package com.example.microservicetelegram.exception;

public class RoomServiceException extends RuntimeException {

    public RoomServiceException() {
        super();
    }

    public RoomServiceException(String message) {
        super(message);
    }
    
}
