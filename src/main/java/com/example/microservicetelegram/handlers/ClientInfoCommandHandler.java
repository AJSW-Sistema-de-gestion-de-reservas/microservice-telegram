package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.dto.ClientInfoResponseDto;
import com.example.microservicetelegram.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientInfoCommandHandler implements CommandHandler {

    private final ClientService clientService;

    @Autowired
    public ClientInfoCommandHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();
        List<SendMessage> messageList = new ArrayList<>();

        ClientInfoResponseDto response = clientService.getInfo(chatId);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("""
                        Tus datos son:
                                                
                        ID: %s
                        ChatID: %s
                        Nombre de usuario: %s
                        Nombre: %s
                        Apellido: %s
                        """.formatted(response.getId(), response.getChatId(), response.getUsername(),
                        response.getFirstName(), response.getLastName()))
                .build();
        messageList.add(sendMessage);

        return messageList;
    }

    @Override
    public boolean canHandle(String command) {
        return command.equals("/misdatos");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {
        
    }
}
