package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.dto.AccommodationDetailsResponseDto;
import com.example.microservicetelegram.dto.BookingInfoResponseDto;
import com.example.microservicetelegram.services.AccommodationService;
import com.example.microservicetelegram.services.BookingService;
import com.example.microservicetelegram.services.OwnerService;
import com.example.microservicetelegram.utils.TimeUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.HTML;

@Component
public class BookingAccommodationOwnerInfoCommandHandler implements CommandHandler {

    private final OwnerService ownerService;
    private final AccommodationService accommodationService;
    private final BookingService bookingService;

    public BookingAccommodationOwnerInfoCommandHandler(OwnerService ownerService,
                                                       AccommodationService accommodationService,
                                                       BookingService bookingService) {
        this.ownerService = ownerService;
        this.accommodationService = accommodationService;
        this.bookingService = bookingService;
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();
        if (!ownerService.existsByChatId(chatId)) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No sos un administrador de alojamientos. Podés registrarte como administrador con el " +
                            "comando /registroadmin")
                    .build();
            return List.of(sendMessage);
        }

        String accommodationId = update.getMessage().getText().split("_")[1];
        Optional<AccommodationDetailsResponseDto> accommodationInfo = accommodationService.getById(accommodationId);
        if (accommodationInfo.isEmpty()) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No se ingreso un alojamiento válido")
                    .build();
            return List.of(sendMessage);
        }

        List<BookingInfoResponseDto> bookings = bookingService.getAllByAccommodation("647a91d0f9e8e13b7de53946");

        if (bookings.isEmpty()) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("El alojamiento <b>%s</b> no tiene reservas".formatted(accommodationInfo.get().getName()))
                    .parseMode(HTML)
                    .build();
            return List.of(sendMessage);
        }

        List<SendMessage> messageList = new ArrayList<>();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Las reservas del alojamiento <b>%s</b>".formatted(accommodationInfo.get().getName()))
                .parseMode(HTML)
                .build();
        messageList.add(sendMessage);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.from(ZoneOffset.UTC));
        bookings.forEach(b -> {
            SendMessage bookingMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("<b>%s</b> - <b>%s</b>\n\nCliente: %s\nPrecio total: $%.2f"
                            .formatted(
                                    formatter.format(TimeUtils.convertInstantDateToUTC(b.getCheckIn())),
                                    formatter.format(TimeUtils.convertInstantDateToUTC(b.getCheckOut())),
                                    b.getClientName(),
                                    b.getAmount()))
                    .parseMode(HTML)
                    .build();
            messageList.add(bookingMessage);
        });

        return messageList;
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("/misreservas_");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {
    }

}
