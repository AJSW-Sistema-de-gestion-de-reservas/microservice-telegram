package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.ClientCreationRequestDto;
import com.example.microservicetelegram.dto.ClientInfoResponseDto;
import com.example.microservicetelegram.exception.ClientConflictException;
import com.example.microservicetelegram.exception.ClientServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ClientServiceImp implements ClientService {

    private final RestTemplate restTemplate;

    @Autowired
    public ClientServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void register(long chatId, String username, String firstName, String lastName)
            throws ClientConflictException, ClientServiceException {
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
            restTemplate.exchange(
                    Endpoints.API_CLIENT_REGISTER,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
            if (e.getStatusCode() == HttpStatus.CONFLICT)
                throw new ClientConflictException();
            else if (e.getStatusCode().is4xxClientError() || e.getStatusCode().is5xxServerError())
                throw new ClientServiceException();
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
        } catch (HttpClientErrorException | HttpServerErrorException e) {
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
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return false;
        }
    }

}
