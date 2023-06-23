package com.example.microservicetelegram.services;

import com.example.microservicetelegram.dto.BookingInfoResponseDto;

import java.util.Date;
import java.util.List;

public interface BookingService {
    boolean book(long chatId, String accommodationId, String roomId, Date checkIn, Date checkOut);

    List<BookingInfoResponseDto> getAllByChatId(long chatId);

    List<BookingInfoResponseDto> getAllByAccommodationAndDate(String accommodationId, Date date);

    List<BookingInfoResponseDto> getAllByAccommodationAndBetweenDate(String accommodationId, Date startDate, Date endDate);
}
