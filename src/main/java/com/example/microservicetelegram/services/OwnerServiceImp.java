package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.OwnerCreationRequestDto;
import com.example.microservicetelegram.dto.OwnerInfoResponseDto;
import com.example.microservicetelegram.exception.OwnerConflictException;
import com.example.microservicetelegram.exception.OwnerServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class OwnerServiceImp implements OwnerService {

    private final RestTemplate restTemplate;

    @Autowired
    public OwnerServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void register(long chatId, String username, String firstName, String lastName)
            throws OwnerConflictException, OwnerServiceException {
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
            restTemplate.exchange(
                    Endpoints.API_OWNER_REGISTER,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();

            if (e.getStatusCode() == HttpStatus.CONFLICT)
                throw new OwnerConflictException();
            else if (e.getStatusCode().is4xxClientError() || e.getStatusCode().is5xxServerError())
                throw new OwnerServiceException();
        }
    }

    @Override
    public Optional<OwnerInfoResponseDto> getInfo(long chatId) {
        try {
            ResponseEntity<OwnerInfoResponseDto> response = restTemplate.exchange(
                    Endpoints.API_OWNER_INFO_FROM_CHAT_ID + chatId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean existsByChatId(long chatId) {
        try {
            ResponseEntity<OwnerInfoResponseDto> response = restTemplate.exchange(
                    Endpoints.API_OWNER_INFO_FROM_CHAT_ID + chatId,
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
