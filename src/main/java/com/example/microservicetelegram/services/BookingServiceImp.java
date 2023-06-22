package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.BookingInfoResponseDto;
import com.example.microservicetelegram.dto.ClientInfoResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingServiceImp implements BookingService {

    private final ClientService clientService;
    private final RestTemplate restTemplate;

    public BookingServiceImp(ClientService clientService) {
        this.clientService = clientService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<BookingInfoResponseDto> getAllByChatId(long chatId) {
        try {
            ClientInfoResponseDto clientInfo = clientService.getInfo(chatId);

            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_BOOKING_CLIENT);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("clientId", clientInfo.getId());

            ResponseEntity<List<BookingInfoResponseDto>> response = restTemplate.exchange(
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

    public List<BookingInfoResponseDto> getAllByAccommodationAndDate(String accommodationId, Date date) {
        try {
            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_BOOKING_ACCOMMODATION_DATE);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("accommodationId", accommodationId);
            pathVariables.put("date", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date.toInstant()));

            ResponseEntity<List<BookingInfoResponseDto>> response = restTemplate.exchange(
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

    public List<BookingInfoResponseDto> getAllByAccommodationAndBetweenDate(String accommodationId, Date startDate, Date endDate) {
        try {
            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_BOOKING_ACCOMMODATION_DATE);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("accommodationId", accommodationId);
            pathVariables.put("startDate", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startDate.toInstant()));
            pathVariables.put("endDate", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(endDate.toInstant()));

            ResponseEntity<List<BookingInfoResponseDto>> response = restTemplate.exchange(
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
