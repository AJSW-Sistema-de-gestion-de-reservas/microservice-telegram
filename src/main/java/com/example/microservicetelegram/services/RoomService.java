package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.RoomInfoResponseDto;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    Optional<RoomInfoResponseDto> getInfo(String accommodationId, String roomId);

    List<RoomInfoResponseDto> getAllByAccommodation(String accommodationId);
}
