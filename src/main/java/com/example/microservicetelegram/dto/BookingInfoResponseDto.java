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
    private String clientId;
    private String accommodationId;
    private String roomId;
    private double amount;
    private boolean paid;
    private Date checkIn;
    private Date checkOut;
    private Date createdAt;
    private Date paymentConfirmedAt;
}
