package com.example.microservicetelegram.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OwnerCreationRequestDto {
    private String username;
    private String firstName;
    private String lastName;
    private long chatId;
}
