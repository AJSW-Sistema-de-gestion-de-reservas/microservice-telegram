package com.example.microservicetelegram.exception;

public class OwnerServiceException extends RuntimeException {

    public OwnerServiceException() {
        super();
    }

    public OwnerServiceException(String message) {
        super(message);
    }

}
