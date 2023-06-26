package com.example.microservicetelegram.exception;

public class ClientServiceException extends RuntimeException {

    public ClientServiceException() {
        super();
    }

    public ClientServiceException(String message) {
        super(message);
    }

}
