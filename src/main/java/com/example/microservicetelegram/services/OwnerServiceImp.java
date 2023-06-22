package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.OwnerCreationRequestDto;
import com.example.microservicetelegram.dto.OwnerInfoResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class OwnerServiceImp implements OwnerService {

    private final RestTemplate restTemplate;

    public OwnerServiceImp() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean register(long chatId, String username, String firstName, String lastName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        OwnerCreationRequestDto requestDto = OwnerCreationRequestDto.builder()
                .chatId(chatId)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        HttpEntity<OwnerCreationRequestDto> entity = new HttpEntity<>(requestDto, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    Endpoints.API_CLIENT_REGISTER,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            return false;
        }
    }

    @Override
    public OwnerInfoResponseDto getInfo(long chatId) {
        try {
            ResponseEntity<OwnerInfoResponseDto> response = restTemplate.exchange(
                    Endpoints.API_CLIENT_INFO_FROM_CHAT_ID + chatId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    public boolean checkUserExists(long chatId) {
        try {
            ResponseEntity<OwnerInfoResponseDto> response = restTemplate.exchange(
                    Endpoints.API_CLIENT_INFO_FROM_CHAT_ID + chatId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            return false;
        }
    }
}
