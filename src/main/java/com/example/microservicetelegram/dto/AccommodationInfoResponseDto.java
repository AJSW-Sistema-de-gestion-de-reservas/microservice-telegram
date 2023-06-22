package com.example.microservicetelegram.dto;

import lombok.Data;

@Data
public class AccommodationInfoResponseDto {
    private String id;
    private String name;
    private String address;
    private String city;
    private String province;
    private String postalCode;
}
