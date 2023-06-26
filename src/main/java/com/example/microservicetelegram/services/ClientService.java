package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.ClientInfoResponseDto;
import com.example.microservicetelegram.exception.ClientConflictException;
import com.example.microservicetelegram.exception.ClientServiceException;

import java.util.Optional;

public interface ClientService {
    void register(long chatId, String username, String firstName, String lastName)
            throws ClientConflictException, ClientServiceException;

    Optional<ClientInfoResponseDto> getInfo(long chatId);

    boolean existsByChatId(long chatId);
}
