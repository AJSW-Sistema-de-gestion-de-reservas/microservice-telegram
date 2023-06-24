package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.OwnerInfoResponseDto;

import java.util.Optional;

public interface OwnerService {
    boolean register(long chatId, String username, String firstName, String lastName);

    Optional<OwnerInfoResponseDto> getInfo(long chatId);

    boolean existsByChatId(long chatId);
}
