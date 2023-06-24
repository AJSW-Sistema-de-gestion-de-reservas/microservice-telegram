package com.example.microservicetelegram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingInfoResponseDto {
    private String id;
    private String clientName;
    private String clientId;
    private String accommodationName;
    private String accommodationId;
    private String roomName;
    private String roomId;
    private double amount;
    private Date checkIn;
    private Date checkOut;
}
