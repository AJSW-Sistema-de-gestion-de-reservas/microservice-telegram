package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.domain.SearchUserData;
import com.example.microservicetelegram.dto.AccommodationInfoResponseDto;
import com.example.microservicetelegram.services.AccommodationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.HTML;

@Component
public class SearchCommandHandler implements CommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationCommandHandler.class);

    private final AccommodationService accommodationService;
    private final Map<Long, SearchUserData> searchUserDataMap;

    @Autowired
    public SearchCommandHandler(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
        this.searchUserDataMap = new HashMap<>();
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();

        if (!searchUserDataMap.containsKey(chatId)) {
            searchUserDataMap.put(chatId, new SearchUserData());
        }

        List<SendMessage> messageList = new ArrayList<>();
        SearchUserData userData = searchUserDataMap.get(chatId);
        switch (userData.getStatus()) {
            case START -> handleStart(update, messageList, userData);
            case CITY -> handleCity(update, messageList, userData);
        }

        return messageList;
    }

    private void handleStart(Update update, List<SendMessage> messageList, SearchUserData userData) {
        long chatId = update.getMessage().getChatId();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Comenzando con la búsqueda!\n\nIngrese la ciudad en la que quiere encontrar los alojamientos:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(SearchUserData.SearchStatus.CITY);
    }

    private void handleCity(Update update, List<SendMessage> messageList, SearchUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        String city = update.getMessage().getText();
        userData.setCity(city);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Mostrando todos los resultado de la búsqueda en \"%s\"".formatted(city))
                .build();
        messageList.add(sendMessage);

        List<AccommodationInfoResponseDto> accommodations = accommodationService.getAllBy(city);

        accommodations.forEach(a -> {
            SendMessage accommodationMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("<b>%s</b>\n\n%s\n%s, %s\n\nPara ver más información: /alojamiento_%s".formatted(a.getName(), a.getAddress(), a.getCity(), a.getProvince(), a.getId()))
                    .parseMode(HTML)
                    .build();
            messageList.add(accommodationMessage);
        });

        searchUserDataMap.remove(chatId);
    }

    @Override
    public boolean canHandle(String command) {
        return command.equals("/buscar");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return searchUserDataMap.containsKey(chatId);
    }

    @Override
    public void removeUserData(long chatId) {
        searchUserDataMap.remove(chatId);
    }
}
