package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.RoomInfoResponseDto;

import java.util.Optional;

public interface RoomService {
    RoomInfoResponseDto getRoom(String roomId);
    Optional<RoomInfoResponseDto> getRoomInfo(String accommodationId, String roomId);

}
