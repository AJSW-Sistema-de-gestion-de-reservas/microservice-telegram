package com.example.microservicetelegram.services;

import com.example.microservicetelegram.config.Endpoints;
import com.example.microservicetelegram.dto.BookingCreationRequestDto;
import com.example.microservicetelegram.dto.BookingInfoResponseDto;
import com.example.microservicetelegram.dto.ClientInfoResponseDto;
import com.example.microservicetelegram.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BookingServiceImp implements BookingService {

    private final ClientService clientService;
    private final RestTemplate restTemplate;

    @Autowired
    public BookingServiceImp(ClientService clientService, RestTemplate restTemplate) {
        this.clientService = clientService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void book(long chatId, String accommodationId, String roomId, Date checkIn, Date checkOut)
            throws AccommodationConflictException, AccommodationServiceException, ClientNotFoundException {
        try {
            Optional<ClientInfoResponseDto> clientInfo = clientService.getInfo(chatId);
            if (clientInfo.isEmpty())
                throw new ClientNotFoundException();

            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_BOOKING_CREATE);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("accommodationId", accommodationId);
            pathVariables.put("roomId", roomId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            BookingCreationRequestDto request = BookingCreationRequestDto.builder()
                    .clientId(clientInfo.get().getId())
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .build();

            HttpEntity<BookingCreationRequestDto> entity = new HttpEntity<>(request, headers);

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
                throw new BookingConflictException();
            else if (e.getStatusCode().is4xxClientError() || e.getStatusCode().is5xxServerError())
                throw new BookingServiceException();
        }
    }

    @Override
    public List<BookingInfoResponseDto> getAllByChatId(long chatId) {
        try {
            Optional<ClientInfoResponseDto> clientInfo = clientService.getInfo(chatId);
            if (clientInfo.isEmpty())
                return List.of();

            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_BOOKING_CLIENT);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("clientId", clientInfo.get().getId());

            ResponseEntity<List<BookingInfoResponseDto>> response = restTemplate.exchange(
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
    public List<BookingInfoResponseDto> getAllByAccommodation(String accommodationId) {
        try {
            UriTemplate uriTemplate = new UriTemplate(Endpoints.API_BOOKING_ACCOMMODATION);
            Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("accommodationId", accommodationId);

            ResponseEntity<List<BookingInfoResponseDto>> response = restTemplate.exchange(
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
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
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
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
