package com.example.microservicetelegram.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface CommandHandler {
    List<SendMessage> handle(Update update);

    boolean canHandle(String command);

    boolean hasUserData(long chatId);

    void removeUserData(long chatId);
}
