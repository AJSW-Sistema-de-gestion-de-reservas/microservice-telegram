package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.ClientCreationRequestDto;
import com.example.microservicetelegram.dto.ClientInfoResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ClientServiceImp implements ClientService {

    private final RestTemplate restTemplate;

    public ClientServiceImp() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean register(long chatId, String username, String firstName, String lastName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        ClientCreationRequestDto requestDto = ClientCreationRequestDto.builder()
                .chatId(chatId)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        HttpEntity<ClientCreationRequestDto> entity = new HttpEntity<>(requestDto, headers);

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
    public Optional<ClientInfoResponseDto> getInfo(long chatId) {
        try {
            ResponseEntity<ClientInfoResponseDto> response = restTemplate.exchange(
                    Endpoints.API_CLIENT_INFO_FROM_CHAT_ID + chatId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }
    }

    public boolean existsByChatId(long chatId) {
        try {
            ResponseEntity<ClientInfoResponseDto> response = restTemplate.exchange(
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
