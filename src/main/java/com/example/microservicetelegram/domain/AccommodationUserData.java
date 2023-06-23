package com.example.microservicetelegram.domain;

import lombok.Data;

@Data
public class AccommodationUserData {
    private AccommodationStatus status;
    private String name;
    private String address;
    private String city;
    private String province;
    private String postalCode;

    public AccommodationUserData() {
        this.status = AccommodationStatus.START;
    }

    public enum AccommodationStatus {
        START,
        NAME,
        ADDRESS,
        CITY,
        PROVINCE,
        POSTAL_CODE,
        CONFIRM
    }
}
