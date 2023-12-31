package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.dto.BookingInfoResponseDto;
import com.example.microservicetelegram.services.BookingService;
import com.example.microservicetelegram.services.ClientService;
import com.example.microservicetelegram.utils.TimeUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class BookingClientInfoCommandHandler implements CommandHandler {

    private final ClientService clientService;
    private final BookingService bookingService;

    private BookingClientInfoCommandHandler(ClientService clientService, BookingService bookingService) {
        this.clientService = clientService;
        this.bookingService = bookingService;
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();

        if (!clientService.existsByChatId(chatId)) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("Tenés que estar registrado para poder ver tus reservas. Registrate con el comando /registro")
                    .build();
            return List.of(sendMessage);
        }

        List<BookingInfoResponseDto> bookings = bookingService.getAllByChatId(chatId);

        if (bookings.size() == 0) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No tenés reservas registradas")
                    .build();
            return List.of(sendMessage);
        }

        List<SendMessage> messageList = new ArrayList<>();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Tus reservas son:")
                .build();
        messageList.add(sendMessage);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.from(ZoneOffset.UTC));
        bookings.forEach(b -> {
            SendMessage bookingMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("%s - %s\n\nAlojamiento: %s\nHabitación: %s\nPrecio total: $%.2f"
                            .formatted(
                                    formatter.format(TimeUtils.convertInstantDateToUTC(b.getCheckIn())),
                                    formatter.format(TimeUtils.convertInstantDateToUTC(b.getCheckOut())),
                                    b.getAccommodationName(),
                                    b.getRoomName(),
                                    b.getAmount())
                    )
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
