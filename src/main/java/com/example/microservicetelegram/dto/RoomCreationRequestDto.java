package com.example.microservicetelegram.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RoomCreationRequestDto {
    private String name;
    private int maxPeople;
    private int quantity;
    private double price;
}
