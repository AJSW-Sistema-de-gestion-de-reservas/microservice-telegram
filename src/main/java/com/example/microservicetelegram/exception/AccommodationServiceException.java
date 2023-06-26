package com.example.microservicetelegram.exception;

public class AccommodationServiceException extends RuntimeException {

    public AccommodationServiceException() {
        super();
    }

    public AccommodationServiceException(String message) {
        super(message);
    }

}
