package com.example.microservicetelegram.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class RoomCreationUserHandler implements CommandHandler {
    @Override
    public List<SendMessage> handle(Update update) {
        return null;
    }

    @Override
    public boolean canHandle(String command) {
        return false;
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {

    }
}
