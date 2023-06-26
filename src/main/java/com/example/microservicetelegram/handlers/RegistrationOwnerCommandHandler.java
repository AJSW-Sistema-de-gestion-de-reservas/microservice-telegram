package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.domain.RegistrationUserData;
import com.example.microservicetelegram.services.OwnerService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RegistrationOwnerCommandHandler extends RegistrationCommandHandler {

    private final OwnerService ownerService;

    public RegistrationOwnerCommandHandler(OwnerService ownerService) {
        super();
        this.ownerService = ownerService;
    }

    @Override
    boolean isUserRegistered(long chatId) {
        return ownerService.existsByChatId(chatId);
    }

    @Override
    boolean register(long chatId, RegistrationUserData userData) {
        try {
            ownerService.register(chatId, userData.getUsername(), userData.getFirstName(), userData.getLastName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canHandle(String command) {
        return Objects.equals(command, "/registroadmin");
    }
}
