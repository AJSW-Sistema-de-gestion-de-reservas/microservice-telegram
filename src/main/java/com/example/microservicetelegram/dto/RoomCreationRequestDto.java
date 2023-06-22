package com.example.microservicetelegram.dto;

import lombok.Data;

@Data
public class RoomCreationRequestDto {
    private String name;
    private int quantity;
}
