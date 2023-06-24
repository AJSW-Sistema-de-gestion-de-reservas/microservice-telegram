package com.example.microservicetelegram.handlers;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Component
public class CancelCommandHandler implements CommandHandler {
    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();

        List<SendMessage> messageList = new ArrayList<>();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Su comando ha sido cancelado!")
                .build();
        messageList.add(sendMessage);

        return messageList;
    }
    @Override
    public boolean canHandle(String command) {
        return Objects.equals(command, "/cancelar");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {
    }
}
