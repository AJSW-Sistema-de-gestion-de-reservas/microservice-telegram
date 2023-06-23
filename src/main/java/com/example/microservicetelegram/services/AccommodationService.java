package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.AccommodationDetailsResponseDto;
import com.example.microservicetelegram.dto.AccommodationInfoResponseDto;

import java.util.List;
import java.util.Optional;

public interface AccommodationService {
    boolean create(long chatId, String name, String address, String city, String province, String postalCode);

    List<AccommodationInfoResponseDto> getAllByCity(String city);

    List<AccommodationInfoResponseDto> getAllByOwner(long chatId);

    Optional<AccommodationDetailsResponseDto> getById(String accommodationId);
}
