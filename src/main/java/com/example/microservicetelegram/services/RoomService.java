package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.RoomInfoResponseDto;
import com.example.microservicetelegram.exception.OwnerNotFoundException;
import com.example.microservicetelegram.exception.RoomConflictException;
import com.example.microservicetelegram.exception.RoomServiceException;

import java.util.List;
import java.util.Optional;

public interface RoomService {


    void create(long chatId, String accommodationId, String name, int maxPeople, int quantity, double price)
            throws RoomConflictException, RoomServiceException, OwnerNotFoundException;

    Optional<RoomInfoResponseDto> getByAccommodationAndId(String accommodationId, String roomId);

    List<RoomInfoResponseDto> getAllByAccommodation(String accommodationId);
}
