package com.example.microservicetelegram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationDetailsResponseDto {
    private String id;
    private String name;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private List<RoomInfoResponseDto> rooms;
}
