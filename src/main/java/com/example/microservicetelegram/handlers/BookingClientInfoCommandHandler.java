package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.dto.BookingInfoResponseDto;
import com.example.microservicetelegram.services.BookingService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class BookingClientInfoCommandHandler implements CommandHandler {

    private final BookingService bookingService;

    private BookingClientInfoCommandHandler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();

        List<SendMessage> messageList = new ArrayList<>();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Tus reservas son:")
                .build();
        messageList.add(sendMessage);

        List<BookingInfoResponseDto> bookings = bookingService.getAllByChatId(123123123L);

        bookings.forEach(b -> {
            SendMessage bookingMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("%s - %s\n\nAlojamiento: %s\nCreada el %s\nEstado: %s"
                            .formatted(b.getCheckIn(), b.getCheckOut(), b.getAccommodationId(), b.getCreatedAt(), (b.isPaid()) ? "Confirmado" : "Pendiente"))
                    .build();
            messageList.add(bookingMessage);
        });

        return messageList;
    }

    @Override
    public boolean canHandle(String command) {
        return Objects.equals(command, "/misreservas");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {

    }
}
