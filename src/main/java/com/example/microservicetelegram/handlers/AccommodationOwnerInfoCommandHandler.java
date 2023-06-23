package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.dto.AccommodationInfoResponseDto;
import com.example.microservicetelegram.services.AccommodationService;
import com.example.microservicetelegram.services.OwnerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.HTML;

@Component
public class AccommodationOwnerInfoCommandHandler implements CommandHandler {

    private final OwnerService ownerService;
    private final AccommodationService accommodationService;

    public AccommodationOwnerInfoCommandHandler(OwnerService ownerService, AccommodationService accommodationService) {
        this.ownerService = ownerService;
        this.accommodationService = accommodationService;
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();

        if (!ownerService.existsByChatId(chatId)) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No estás registrado como administrador. Podés registrarte con el comando /registroadmin")
                    .build();
            return List.of(sendMessage);
        }

        List<AccommodationInfoResponseDto> accommodationsInfo = accommodationService.getAllByOwner(chatId);
        if (accommodationsInfo.isEmpty()) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No tenés alojamientos registrados")
                    .build();
            return List.of(sendMessage);
        }

        List<SendMessage> messageList = new ArrayList<>();
        SendMessage accommodationInfoMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Tus alojamientos son:")
                .build();
        messageList.add(accommodationInfoMessage);

        accommodationsInfo.forEach(a -> {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("<b>%s</b>\n\n%s\n%s, %s".formatted(a.getName(), a.getAddress(), a.getCity(), a.getProvince()))
                    .parseMode(HTML)
                    .build();
            messageList.add(sendMessage);
        });

        return messageList;
    }

    @Override
    public boolean canHandle(String command) {
        return Objects.equals(command, "/misalojamientos");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {
    }

}
