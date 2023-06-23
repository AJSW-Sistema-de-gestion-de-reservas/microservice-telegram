package com.example.microservicetelegram.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class BookingCreationRequestDto {
    private Date checkIn;
    private Date checkOut;
    private String clientId;
}
