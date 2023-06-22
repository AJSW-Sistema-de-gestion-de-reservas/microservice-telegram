package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.domain.RegistrationUserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public abstract class RegistrationCommandHandler implements CommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationCommandHandler.class);

    private final Map<Long, RegistrationUserData> registrationUserDataMap;

    @Autowired
    public RegistrationCommandHandler() {
        this.registrationUserDataMap = new HashMap<>();
    }

    @Override
    public List<SendMessage> handle(Update update) {
        long chatId = 0L;
        if (update.hasMessage())
            chatId = update.getMessage().getChatId();
        else if (update.hasCallbackQuery())
            chatId = update.getCallbackQuery().getMessage().getChatId();

        List<SendMessage> messageList = new ArrayList<>();

        if (!registrationUserDataMap.containsKey(chatId)) {
            if (isUserRegistered(chatId)) {
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(chatId)
                        .text("Ya estás registrado!")
                        .build();
                messageList.add(sendMessage);
                return messageList;
            } else {
                registrationUserDataMap.put(chatId, new RegistrationUserData());
            }
        }

        RegistrationUserData userData = registrationUserDataMap.get(chatId);
        switch (userData.getStatus()) {
            case START -> handleStart(update, messageList, userData);
            case USERNAME -> handleUsername(update, messageList, userData);
            case FIRSTNAME -> handleFirstName(update, messageList, userData);
            case LASTNAME -> handleLastName(update, messageList, userData);
            case CONFIRM -> handleConfirm(update, messageList, userData);
        }

        return messageList;
    }

    private void handleStart(Update update, List<SendMessage> messageList, RegistrationUserData userData) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Comenzando el proceso de registro!\n\nPor favor, ingresa tu nombre de usuario:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(RegistrationUserData.RegistrationStatus.USERNAME);
    }

    private void handleUsername(Update update, List<SendMessage> messageList, RegistrationUserData userData) {
        if (!update.hasMessage())
            return;

        Message message = update.getMessage();
        userData.setUsername(message.getText());

        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Por favor, ingresa tu nombre:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(RegistrationUserData.RegistrationStatus.FIRSTNAME);
    }

    private void handleFirstName(Update update, List<SendMessage> messageList, RegistrationUserData userData) {
        if (!update.hasMessage())
            return;

        Message message = update.getMessage();
        userData.setFirstName(message.getText());

        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Por favor, ingresa tu apellido:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(RegistrationUserData.RegistrationStatus.LASTNAME);
    }

    private void handleLastName(Update update, List<SendMessage> messageList, RegistrationUserData userData) {
        if (!update.hasMessage())
            return;

        Message message = update.getMessage();
        userData.setLastName(message.getText());

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton buttonConfirm = InlineKeyboardButton.builder()
                .text("Confirm")
                .callbackData("y")
                .build();
        InlineKeyboardButton buttonCancel = InlineKeyboardButton.builder()
                .text("Cancel")
                .callbackData("n")
                .build();
        row.add(buttonCancel);
        row.add(buttonConfirm);
        keyboardRows.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboardRows);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .replyMarkup(markup)
                .text("""
                        Por favor, comprobá que los datos ingresados son correctos:
                                                        
                        Nombre de usuario: %s
                        Nombre: %s,
                        Apellido: %s
                        """.formatted(userData.getUsername(), userData.getFirstName(), userData.getLastName()))
                .build();
        messageList.add(sendMessage);

        userData.setStatus(RegistrationUserData.RegistrationStatus.CONFIRM);
    }

    private void handleConfirm(Update update, List<SendMessage> messageList, RegistrationUserData userData) {
        if (!update.hasCallbackQuery())
            return;

        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (Objects.equals(callbackData, "y")) {
            LOGGER.info(userData.toString());

            boolean result = register(chatId, userData);
            SendMessage sendMessage;
            if (result) {
                sendMessage = SendMessage.builder()
                        .chatId(chatId)
                        .text("Registro completado!")
                        .build();
            } else {
                sendMessage = SendMessage.builder()
                        .chatId(chatId)
                        .text("Ha ocurrido un error")
                        .build();
            }
            messageList.add(sendMessage);
        } else {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("Registro cancelado.\n\nPodés volver a iniciar el registro con el comando /registro.")
                    .build();
            messageList.add(sendMessage);
        }

        registrationUserDataMap.remove(chatId);
    }

    abstract boolean isUserRegistered(long chatId);

    abstract boolean register(long chatId, RegistrationUserData userData);

    @Override
    public abstract boolean canHandle(String command);

    @Override
    public boolean hasUserData(long chatId) {
        return registrationUserDataMap.containsKey(chatId);
    }

    @Override
    public void removeUserData(long chatId) {
        registrationUserDataMap.remove(chatId);
    }
}
