package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.ClientInfoResponseDto;

import java.util.Optional;

public interface ClientService {
    boolean register(long chatId, String username, String firstName, String lastName);

    Optional<ClientInfoResponseDto> getInfo(long chatId);

    boolean existsByChatId(long chatId);
}
