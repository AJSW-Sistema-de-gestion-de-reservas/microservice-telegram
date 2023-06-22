package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.AccommodationInfoResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccommodationServiceImp implements AccommodationService {

    private final RestTemplate restTemplate;

    public AccommodationServiceImp() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<AccommodationInfoResponseDto> getBy(String city) {
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
        } catch (HttpClientErrorException e) {
            return List.of();
        }
    }
}
