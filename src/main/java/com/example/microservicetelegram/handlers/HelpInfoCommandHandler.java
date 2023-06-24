package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.services.BookingService;
import com.example.microservicetelegram.services.ClientService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelpInfoCommandHandler implements CommandHandler {

    private  final ClientService clientService;

    private HelpInfoCommandHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();

        List<SendMessage> messageList = new ArrayList<>();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Lista de comandos:\n " +
                        "Cliente\n\t" +
                        "Registrarse: /registro\n\t" +
                        "Consultar datos: /misdatos\n\t" +
                        "Buscar alojamiento: /buscar\n\t" +
                        "Consultar mis reservas: /misreservas\n"+
                        "Administrador\n\t"+
                        "Registrarse: /registroadmin\n\t"+
                        "Consultar datos: /misdatosadmin\n\t"+
                        "Ver alojamientos propios: /misalojamientos\n\t"+
                        "Crear un nuevo alojamiento: /crearalojamiento")
                .build();
        messageList.add(sendMessage);

        return messageList;
    }

    @Override
    public boolean canHandle(String command) {
        return Objects.equals(command, "/ayuda");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {
    }

}

