package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.AccommodationDetailsResponseDto;
import com.example.microservicetelegram.dto.AccommodationInfoResponseDto;

import java.util.List;
import java.util.Optional;

public interface AccommodationService {
    List<AccommodationInfoResponseDto> getAllBy(String city);

    Optional<AccommodationDetailsResponseDto> getBy(String accommodationId);
}
