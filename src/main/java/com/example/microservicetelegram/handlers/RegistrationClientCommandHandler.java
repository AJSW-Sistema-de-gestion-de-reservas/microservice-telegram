package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.domain.RegistrationUserData;
import com.example.microservicetelegram.services.ClientService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RegistrationClientCommandHandler extends RegistrationCommandHandler {

    private final ClientService clientService;

    public RegistrationClientCommandHandler(ClientService clientService) {
        super();
        this.clientService = clientService;
    }

    @Override
    boolean isUserRegistered(long chatId) {
        return clientService.checkUserExists(chatId);
    }

    @Override
    boolean register(long chatId, RegistrationUserData userData) {
        return clientService.register(chatId, userData.getUsername(), userData.getFirstName(), userData.getLastName());
    }

    @Override
    public boolean canHandle(String command) {
        return Objects.equals(command, "/registro");
    }
}
