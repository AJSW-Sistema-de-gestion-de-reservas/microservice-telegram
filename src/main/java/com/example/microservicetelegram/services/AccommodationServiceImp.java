package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.AccommodationCreationWithIdRequestDto;
import com.example.microservicetelegram.dto.AccommodationDetailsResponseDto;
import com.example.microservicetelegram.dto.AccommodationInfoResponseDto;
import com.example.microservicetelegram.dto.OwnerInfoResponseDto;
import com.example.microservicetelegram.exception.AccommodationConflictException;
import com.example.microservicetelegram.exception.AccommodationServiceException;
import com.example.microservicetelegram.exception.OwnerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AccommodationServiceImp implements AccommodationService {

    private final OwnerService ownerService;

    private final RestTemplate restTemplate;

    @Autowired
    public AccommodationServiceImp(OwnerService ownerService, RestTemplate restTemplate) {
        this.ownerService = ownerService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void create(long chatId, String name, String address, String city, String province, String postalCode)
            throws AccommodationConflictException, AccommodationServiceException, OwnerNotFoundException {
        try {
            Optional<OwnerInfoResponseDto> ownerInfo = ownerService.getInfo(chatId);
            if (ownerInfo.isEmpty())
                throw new OwnerNotFoundException();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            AccommodationCreationWithIdRequestDto request = AccommodationCreationWithIdRequestDto.builder()
                    .name(name)
                    .address(address)
                    .city(city)
                    .province(province)
                    .postalCode(postalCode)
                    .ownerId(ownerInfo.get().getId())
                    .build();

            HttpEntity<AccommodationCreationWithIdRequestDto> entity = new HttpEntity<>(request, headers);

            restTemplate.exchange(
                    Endpoints.API_ACCOMMODATION_CREATE,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();

            if (e.getStatusCode() == HttpStatus.CONFLICT)
                throw new AccommodationConflictException();
            else if (e.getStatusCode().is4xxClientError()) {
                throw new AccommodationServiceException();
            } else if (e.getStatusCode().is5xxServerError()) {
                throw new AccommodationServiceException();
            }
        }
    }

    @Override
    public List<AccommodationInfoResponseDto> getAllByCity(String city) {
        try {
            String urlTemplate = UriComponentsBuilder.fromHttpUrl(Endpoints.API_ACCOMMODATION_SEARCH)
                    .queryParam("city", "{city}")
                    .encode()
                    .toUriString();

            Map<String, String> params = new HashMap<>();
            params.put("city", city);

            ResponseEntity<List<AccommodationInfoResponseDto>> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    },
                    params
            );

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<AccommodationInfoResponseDto> getAllByOwner(long chatId) {
        try {
            Optional<OwnerInfoResponseDto> ownerInfo = ownerService.getInfo(chatId);
            if (ownerInfo.isEmpty())
                return List.of();

            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_ACCOMMODATION_SEARCH_OWNER);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("ownerId", ownerInfo.get().getId());

            ResponseEntity<List<AccommodationInfoResponseDto>> response = restTemplate.exchange(
                    uriTemplate.expand(pathVariables),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<AccommodationDetailsResponseDto> getById(String accommodationId) {
        try {
            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_ACCOMMODATION_BY_ID);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("accommodationId", accommodationId);

            ResponseEntity<AccommodationDetailsResponseDto> response = restTemplate.exchange(
                    uriTemplate.expand(pathVariables),

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
}
