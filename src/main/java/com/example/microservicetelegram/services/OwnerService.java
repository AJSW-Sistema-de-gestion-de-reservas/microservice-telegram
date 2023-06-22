package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.OwnerInfoResponseDto;

public interface OwnerService {
    boolean register(long chatId, String username, String firstName, String lastName);

    OwnerInfoResponseDto getInfo(long chatId);

    boolean checkUserExists(long chatId);
}
