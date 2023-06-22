package com.example.microservicetelegram.domain;

import lombok.Data;

@Data
public class RegistrationUserData {
    private RegistrationStatus status;
    private String username;
    private String firstName;
    private String lastName;

    public RegistrationUserData() {
        this.status = RegistrationStatus.START;
    }

    public enum RegistrationStatus {
        START,
        USERNAME,
        FIRSTNAME,
        LASTNAME,
        CONFIRM
    }
}
