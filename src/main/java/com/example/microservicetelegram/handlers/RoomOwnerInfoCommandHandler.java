package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.dto.AccommodationDetailsResponseDto;
import com.example.microservicetelegram.dto.RoomInfoResponseDto;
import com.example.microservicetelegram.services.AccommodationService;
import com.example.microservicetelegram.services.OwnerService;
import com.example.microservicetelegram.services.RoomService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.HTML;

@Component
public class RoomOwnerInfoCommandHandler implements CommandHandler {

    private final OwnerService ownerService;
    private final AccommodationService accommodationService;
    private final RoomService roomService;

    public RoomOwnerInfoCommandHandler(OwnerService ownerService,
                                       AccommodationService accommodationService,
                                       RoomService roomService) {
        this.ownerService = ownerService;
        this.accommodationService = accommodationService;
        this.roomService = roomService;
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();

        String messageText = update.getMessage().getText();
        if (!messageText.contains("_")) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("El comando enviado no es válido")
                    .build();
            return List.of(sendMessage);
        }

        String accommodationId = messageText.split("_")[1];
        Optional<AccommodationDetailsResponseDto> accommodationInfo = accommodationService.getById(accommodationId);
        if (accommodationInfo.isEmpty()) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("El comando enviado no es válido")
                    .build();
            return List.of(sendMessage);
        }

        List<RoomInfoResponseDto> roomsInfo = roomService.getAllByAccommodation(accommodationId);
        if (roomsInfo.isEmpty()) {
            SendMessage emptyMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(("El alojamiento <b>%s</b> no tiene habitaciones. Podés agregar nuevas habitaciones con el " +
                            "comando /crearhabitacion_%s").formatted(accommodationInfo.get().getName(), accommodationId))
                    .parseMode(HTML)
                    .build();
            return List.of(emptyMessage);
        }

        List<SendMessage> messageList = new ArrayList<>();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Las habitaciones del alojamiento <b>%s</b> son:".formatted(accommodationInfo.get().getName()))
                .parseMode(HTML)
                .build();
        messageList.add(sendMessage);

        roomsInfo.forEach(r -> {
            SendMessage roomMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("<b>%s</b> - $%s\n\nCantidad de personas: %s\nCantidad de habitaciónes: %s"
                            .formatted(r.getName(), r.getPrice(), r.getMaxPeople(), r.getQuantity()))
                    .parseMode(HTML)
                    .build();
            messageList.add(roomMessage);
        });

        SendMessage addMoreMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Podés agregar habitaciones al alojamiento con el comando /crearhabitacion_%s\n"
                        .formatted(accommodationId))
                .build();
        messageList.add(addMoreMessage);

        return messageList;
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("/mishabitaciones_");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {
    }

}
