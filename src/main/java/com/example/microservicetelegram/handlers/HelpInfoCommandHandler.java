package com.example.microservicetelegram.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN;

@Component
public class HelpInfoCommandHandler implements CommandHandler {

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage())
            return List.of();

        long chatId = update.getMessage().getChatId();

        List<SendMessage> messageList = new ArrayList<>();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("""
                        **Lista de comandos**
                                                 
                        **Cliente**
                        - Registrarse: /registro
                        - Consultar mis datos: /misdatos
                        - Buscar alojamiento: /buscar
                        - Consultar mis reservas: /misreservas
                                                 
                        **Administrador de alojamientos**
                        - Registrarse: /registroadmin
                        - Consultar mis datos: /misdatosadmin
                        - Ver mis alojamientos: /misalojamientos
                        - Agregar un nuevo alojamiento: /crearalojamiento
                                                
                        **Otros**
                        - Cancelar el comando en curso: /cancelar
                        - Ver la lista de comandos disponibles: /ayuda
                        """)
                .parseMode(MARKDOWN)
                .build();
        messageList.add(sendMessage);

        return messageList;
    }

    @Override
    public boolean canHandle(String command) {
        return Objects.equals(command, "/ayuda") ||
                Objects.equals(command, "/help") ||
                Objects.equals(command, "/start");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return false;
    }

    @Override
    public void removeUserData(long chatId) {
    }

}

