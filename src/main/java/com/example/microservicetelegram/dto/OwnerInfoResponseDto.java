package com.example.microservicetelegram.dto;

import lombok.Data;

@Data
public class OwnerInfoResponseDto {
    private String id;
    private String username;
    private long chatId;
    private String firstName;
    private String lastName;
}
