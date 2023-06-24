package com.example.microservicetelegram.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccommodationCreationWithIdRequestDto {
    private String name;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String ownerId;
}
