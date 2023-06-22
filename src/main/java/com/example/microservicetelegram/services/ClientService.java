package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.ClientInfoResponseDto;

public interface ClientService {
    boolean register(long chatId, String username, String firstName, String lastName);

    ClientInfoResponseDto getInfo(long chatId);

    boolean checkUserExists(long chatId);
}
