package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.BookingInfoResponseDto;
import com.example.microservicetelegram.exception.AccommodationConflictException;
import com.example.microservicetelegram.exception.AccommodationServiceException;
import com.example.microservicetelegram.exception.ClientNotFoundException;

import java.util.Date;
import java.util.List;

public interface BookingService {
    void book(long chatId, String accommodationId, String roomId, Date checkIn, Date checkOut)
            throws AccommodationConflictException, AccommodationServiceException, ClientNotFoundException;

    List<BookingInfoResponseDto> getAllByChatId(long chatId);

    List<BookingInfoResponseDto> getAllByAccommodation(String accommodationId);

    List<BookingInfoResponseDto> getAllByAccommodationAndDate(String accommodationId, Date date);

    List<BookingInfoResponseDto> getAllByAccommodationAndBetweenDate(String accommodationId, Date startDate, Date endDate);
}
