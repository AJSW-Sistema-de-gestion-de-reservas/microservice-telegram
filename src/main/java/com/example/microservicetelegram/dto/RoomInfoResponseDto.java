package com.example.microservicetelegram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoResponseDto {
    private String id;
    private String name;
    private int quantity;
}
