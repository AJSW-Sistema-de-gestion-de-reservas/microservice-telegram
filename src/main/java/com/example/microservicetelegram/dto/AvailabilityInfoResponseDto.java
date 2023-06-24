package com.example.microservicetelegram.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class AvailabilityInfoResponseDto {
    private String id;
    private Date date;
    private int availableQuantity;
    private String accommodationId;
    private String roomId;
}
