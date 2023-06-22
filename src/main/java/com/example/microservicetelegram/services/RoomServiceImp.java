package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.RoomInfoResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class RoomServiceImp implements RoomService {

    private final RestTemplate restTemplate;

    public RoomServiceImp() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Optional<RoomInfoResponseDto> getRoomInfo(String accommodationId, String roomId) {
        try {
            ResponseEntity<RoomInfoResponseDto> response = restTemplate.exchange(
                    Endpoints.API_ACCOMMODATION_BY_ID + accommodationId,
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
}
