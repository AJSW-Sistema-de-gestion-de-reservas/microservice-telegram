package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.AccommodationInfoResponseDto;

import java.util.List;

public interface AccommodationService {
    List<AccommodationInfoResponseDto> getBy(String city);
}
