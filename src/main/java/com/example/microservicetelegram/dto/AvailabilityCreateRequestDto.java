package com.example.microservicetelegram.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class AvailabilityCreateRequestDto {
    private Date date;
    private double price;
}
