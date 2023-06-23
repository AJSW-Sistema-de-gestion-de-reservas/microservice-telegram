package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.dto.RoomInfoResponseDto;
import com.example.microservicetelegram.services.OwnerService;
import com.example.microservicetelegram.services.RoomService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoomOwnerInfoCommandHandler implements CommandHandler {

    private final OwnerService ownerService;
    private final RoomService roomService;

    public RoomOwnerInfoCommandHandler(OwnerService ownerService, RoomService roomService) {
        this.ownerService = ownerService;
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
        List<RoomInfoResponseDto> roomsInfo = roomService.getAllByAccommodation(accommodationId);

        if (roomsInfo.isEmpty()) {
            SendMessage emptyMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("El alojamiento no tiene habitaciones. Podés agregar nuevas habitaciones con el comando " +
                            "/crearhabitacion_%s".formatted(accommodationId))
                    .build();
            return List.of(emptyMessage);
        }

        List<SendMessage> messageList = new ArrayList<>();
        roomsInfo.forEach(r -> {
            SendMessage roomMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("<b>%s</b> - $%s\n\nCantidad de personas: %s\nCantidad de habitaciónes: %s"
                            .formatted(r.getName(), r.getPrice(), r.getMaxPeople(), r.getQuantity()))
                    .build();
            messageList.add(roomMessage);
        });

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
