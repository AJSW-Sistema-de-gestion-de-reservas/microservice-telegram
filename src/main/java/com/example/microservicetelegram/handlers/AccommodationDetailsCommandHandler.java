package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.dto.AccommodationDetailsResponseDto;
import com.example.microservicetelegram.services.AccommodationService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.HTML;

@Component
public class AccommodationDetailsCommandHandler implements CommandHandler {

    private final AccommodationService accommodationService;

    public AccommodationDetailsCommandHandler(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();
        List<SendMessage> messageList = new ArrayList<>();

        if (!update.getMessage().hasText() || !update.getMessage().getText().contains("_")) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("El alojamiento seleccionado no es válido")
                    .build();
            messageList.add(sendMessage);
            return messageList;
        }

        String text = update.getMessage().getText();
        String accommodationId = text.substring(text.indexOf("_") + 1).trim();

        Optional<AccommodationDetailsResponseDto> result = accommodationService.getBy(accommodationId);
        if (result.isEmpty()) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No se ha encontrado información del alojamiento")
                    .build();
            messageList.add(sendMessage);
            return messageList;
        }

        AccommodationDetailsResponseDto details = result.get();

        SendMessage accommodationDetailsMessage = SendMessage.builder()
                .chatId(chatId)
                .parseMode(HTML)
                .text("<b>%s</b>\n\n%s\n%s, %s".formatted(details.getName(), details.getAddress(), details.getCity(), details.getProvince()))
                .build();
        messageList.add(accommodationDetailsMessage);

        SendMessage roomsMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Las habitaciones disponibles son:")
                .build();
        messageList.add(roomsMessage);

        details.getRooms().forEach(r -> {
            SendMessage roomMessage = SendMessage.builder()
                    .chatId(chatId)
                    .parseMode(HTML)
                    .text("<b>%s</b>\n\nPara realizar una reserva: /reservar_%s_%s".formatted(r.getName(), details.getId(), r.getId()))
                    .build();
            messageList.add(roomMessage);
        });

        return messageList;
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("/alojamiento_");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {
    }
}
