package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.OwnerInfoResponseDto;
import com.example.microservicetelegram.dto.RoomCreationRequestDto;
import com.example.microservicetelegram.dto.RoomInfoResponseDto;
import com.example.microservicetelegram.exception.OwnerNotFoundException;
import com.example.microservicetelegram.exception.RoomConflictException;
import com.example.microservicetelegram.exception.RoomServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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

    @Autowired
    public RoomServiceImp(OwnerService ownerService, RestTemplate restTemplate) {
        this.ownerService = ownerService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void create(long chatId, String accommodationId, String name, int maxPeople, int quantity, double price)
            throws RoomConflictException, RoomServiceException, OwnerNotFoundException {
        try {
            Optional<OwnerInfoResponseDto> ownerInfo = ownerService.getInfo(chatId);
            if (ownerInfo.isEmpty())
                throw new OwnerNotFoundException();

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

            restTemplate.exchange(
                    uriTemplate.expand(pathVariables),
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();

            if (e.getStatusCode() == HttpStatus.CONFLICT)
                throw new RoomConflictException();
            else if (e.getStatusCode().is4xxClientError() || e.getStatusCode().is5xxServerError())
                throw new RoomServiceException();
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
        } catch (HttpClientErrorException | HttpServerErrorException e) {
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
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return List.of();
        }
    }
}
