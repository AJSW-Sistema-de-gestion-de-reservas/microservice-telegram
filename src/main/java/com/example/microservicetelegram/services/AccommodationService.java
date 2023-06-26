package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.AccommodationDetailsResponseDto;
import com.example.microservicetelegram.dto.AccommodationInfoResponseDto;
import com.example.microservicetelegram.exception.AccommodationConflictException;
import com.example.microservicetelegram.exception.AccommodationServiceException;
import com.example.microservicetelegram.exception.ClientNotFoundException;

import java.util.List;
import java.util.Optional;

public interface AccommodationService {
    void create(long chatId, String name, String address, String city, String province, String postalCode)
            throws AccommodationConflictException, AccommodationServiceException, ClientNotFoundException;

    List<AccommodationInfoResponseDto> getAllByCity(String city);

    List<AccommodationInfoResponseDto> getAllByOwner(long chatId);

    Optional<AccommodationDetailsResponseDto> getById(String accommodationId);
}
