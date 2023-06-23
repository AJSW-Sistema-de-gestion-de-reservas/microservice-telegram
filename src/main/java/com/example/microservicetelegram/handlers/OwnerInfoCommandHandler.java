package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.dto.OwnerInfoResponseDto;
import com.example.microservicetelegram.services.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class OwnerInfoCommandHandler implements CommandHandler {

    private final OwnerService ownerService;

    @Autowired
    public OwnerInfoCommandHandler(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();
        List<SendMessage> messageList = new ArrayList<>();

        Optional<OwnerInfoResponseDto> response = ownerService.getInfo(chatId);
        if (response.isPresent()) {
            OwnerInfoResponseDto ownerInfo = response.get();
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("""
                            Tus datos son:
                                                        
                            Nombre de usuario: %s
                            Nombre: %s
                            Apellido: %s
                            """.formatted(ownerInfo.getUsername(), ownerInfo.getFirstName(), ownerInfo.getLastName()))
                    .build();
            messageList.add(sendMessage);
        } else {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No estás registrado como administrador de alojamientos. " +
                            "Podés registrarte como administrador con el comando /registro")
                    .build();
            messageList.add(sendMessage);
        }

        return messageList;
    }

    @Override
    public boolean canHandle(String command) {
        return Objects.equals(command, "/misdatosadmin");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {
    }

}
