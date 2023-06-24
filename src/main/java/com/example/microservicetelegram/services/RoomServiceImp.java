package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.OwnerInfoResponseDto;
import com.example.microservicetelegram.dto.RoomCreationRequestDto;
import com.example.microservicetelegram.dto.RoomInfoResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RoomServiceImp implements RoomService {

    private final OwnerService ownerService;

    private final RestTemplate restTemplate;

    public RoomServiceImp(OwnerService ownerService) {
        this.ownerService = ownerService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean create(long chatId, String accommodationId, String name, int maxPeople, int quantity, double price) {
        try {
            Optional<OwnerInfoResponseDto> ownerInfo = ownerService.getInfo(chatId);
            if (ownerInfo.isEmpty())
                return false;


            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            RoomCreationRequestDto request = RoomCreationRequestDto.builder()
                    .name(name)
                    .maxPeople(maxPeople)
                    .quantity(quantity)
                    .price(price)
                    .build();

            HttpEntity<RoomCreationRequestDto> entity = new HttpEntity<>(request, headers);

            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_ROOM);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("accommodationId", accommodationId);

            ResponseEntity<String> response = restTemplate.exchange(
                    uriTemplate.expand(pathVariables),
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<RoomInfoResponseDto> getByAccommodationAndId(String accommodationId, String roomId) {
        try {
            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_ROOM_BY_ID);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("accommodationId", accommodationId);
            pathVariables.put("roomId", roomId);

            System.out.println(uriTemplate.expand(pathVariables));

            ResponseEntity<RoomInfoResponseDto> response = restTemplate.exchange(
                    uriTemplate.expand(pathVariables),
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

    @Override
    public List<RoomInfoResponseDto> getAllByAccommodation(String accommodationId) {
        try {
            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_ROOM);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("accommodationId", accommodationId);

            ResponseEntity<List<RoomInfoResponseDto>> response = restTemplate.exchange(
                    uriTemplate.expand(pathVariables),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            return List.of();
        }
    }
}
