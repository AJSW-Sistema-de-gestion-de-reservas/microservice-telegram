package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.RoomInfoResponseDto;

import java.util.List;
import java.util.Optional;

public interface RoomService {


    boolean create(long chatId, String accommodationId, String name, int maxPeople, int quantity, double price);

    Optional<RoomInfoResponseDto> getByAccommodationAndId(String accommodationId, String roomId);

    List<RoomInfoResponseDto> getAllByAccommodation(String accommodationId);
}
