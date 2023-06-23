package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.RoomInfoResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RoomServiceImp implements RoomService {

    private final RestTemplate restTemplate;

    public RoomServiceImp() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Optional<RoomInfoResponseDto> getInfo(String accommodationId, String roomId) {
        try {
            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_ROOM_BY_ID);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("accommodationId", accommodationId);
            pathVariables.put("roomId", roomId);

            ResponseEntity<Optional<RoomInfoResponseDto>> response = restTemplate.exchange(
                    uriTemplate.expand(pathVariables),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }
    }
}
