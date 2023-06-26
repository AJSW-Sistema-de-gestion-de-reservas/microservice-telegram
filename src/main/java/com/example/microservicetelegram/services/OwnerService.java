package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.OwnerInfoResponseDto;
import com.example.microservicetelegram.exception.OwnerConflictException;
import com.example.microservicetelegram.exception.OwnerServiceException;

import java.util.Optional;

public interface OwnerService {
    void register(long chatId, String username, String firstName, String lastName)
            throws OwnerConflictException, OwnerServiceException;

    Optional<OwnerInfoResponseDto> getInfo(long chatId);

    boolean existsByChatId(long chatId);
}
