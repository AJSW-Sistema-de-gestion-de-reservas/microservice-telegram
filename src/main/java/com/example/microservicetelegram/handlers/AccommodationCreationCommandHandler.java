package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.domain.AccommodationUserData;
import com.example.microservicetelegram.services.AccommodationService;
import com.example.microservicetelegram.services.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Component
public class AccommodationCreationCommandHandler implements CommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccommodationCreationCommandHandler.class);

    private static final String CALLBACK_DATA_CANCEL = "N";
    private static final String CALLBACK_DATA_CONFIRM = "Y";

    private final OwnerService ownerService;
    private final AccommodationService accommodationService;

    private final Map<Long, AccommodationUserData> accommodationUserDataMap;

    private AccommodationCreationCommandHandler(OwnerService ownerService, AccommodationService accommodationService) {
        this.ownerService = ownerService;
        this.accommodationService = accommodationService;
        this.accommodationUserDataMap = new HashMap<>();
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage() && !update.hasCallbackQuery())
            return List.of();

        long chatId = (update.hasMessage()) ? update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();

        if (!accommodationUserDataMap.containsKey(chatId)) {
            if (!ownerService.existsByChatId(chatId)) {
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(chatId)
                        .text("Tenés que estar registrado como administrador para añadir un alojamiento. " +
                                "Registrate con el comando /registroadmin")
                        .build();
                return List.of(sendMessage);
            }

            accommodationUserDataMap.put(chatId, new AccommodationUserData());
        }

        List<SendMessage> messageList = new ArrayList<>();
        AccommodationUserData userData = accommodationUserDataMap.get(chatId);
        switch (userData.getStatus()) {
            case START -> handleStart(update, messageList, userData);
            case NAME -> handleName(update, messageList, userData);
            case ADDRESS -> handleAddress(update, messageList, userData);
            case CITY -> handleCity(update, messageList, userData);
            case PROVINCE -> handleProvince(update, messageList, userData);
            case POSTAL_CODE -> handlePostalCode(update, messageList, userData);
            case CONFIRM -> handleConfirm(update, messageList, userData);
        }

        return messageList;
    }

    private void handleStart(Update update, List<SendMessage> messageList, AccommodationUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Comenzando con la creación de un alojamiento.\n\n" +
                        "Por favor, ingrese el nombre del alojamiento:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(AccommodationUserData.AccommodationStatus.NAME);
    }

    private void handleName(Update update, List<SendMessage> messageList, AccommodationUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        String name = update.getMessage().getText();
        userData.setName(name);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Por favor, ingrese la dirección:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(AccommodationUserData.AccommodationStatus.ADDRESS);
    }

    private void handleAddress(Update update, List<SendMessage> messageList, AccommodationUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        String address = update.getMessage().getText();
        userData.setAddress(address);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Por favor, ingresa la ciudad en la que se encuentra el alojamiento:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(AccommodationUserData.AccommodationStatus.CITY);
    }

    private void handleCity(Update update, List<SendMessage> messageList, AccommodationUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        String city = update.getMessage().getText();
        userData.setCity(city);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Por favor, ingresa la provincia en la que se encuentra el alojamiento:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(AccommodationUserData.AccommodationStatus.PROVINCE);
    }

    private void handleProvince(Update update, List<SendMessage> messageList, AccommodationUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        String province = update.getMessage().getText();
        userData.setProvince(province);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Por favor, ingresa el código postal del alojamiento:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(AccommodationUserData.AccommodationStatus.POSTAL_CODE);
    }

    private void handlePostalCode(Update update, List<SendMessage> messageList, AccommodationUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        String postalCode = update.getMessage().getText();
        userData.setPostalCode(postalCode);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("Cancelar")
                .callbackData(CALLBACK_DATA_CANCEL)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text("Confirmar")
                .callbackData(CALLBACK_DATA_CONFIRM)
                .build());
        keyboard.add(row);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("""
                        Confirma los datos del alojamiento:
                                                
                        Nombre: %s
                        Dirección: %s
                        Ciudad: %s
                        Provincia: %s
                        Código postal: %s
                        """.formatted(userData.getName(), userData.getAddress(), userData.getCity(),
                        userData.getProvince(), userData.getPostalCode()))
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(keyboard).build())
                .build();
        messageList.add(sendMessage);

        userData.setStatus(AccommodationUserData.AccommodationStatus.CONFIRM);
    }

    private void handleConfirm(Update update, List<SendMessage> messageList, AccommodationUserData userData) {
        if (!update.hasCallbackQuery())
            return;

        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (Objects.equals(callbackData, CALLBACK_DATA_CONFIRM)) {
            LOGGER.info(userData.toString());

            boolean result = accommodationService.create(
                    chatId,
                    userData.getName(),
                    userData.getAddress(),
                    userData.getCity(),
                    userData.getProvince(),
                    userData.getPostalCode()
            );

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text((result) ? "Alojamiento creado con éxito! Podés ver tus alojamientos con el comando " +
                            "/misalojamientos" : "Ha ocurrido un error al crear el alojamiento")
                    .build();
            messageList.add(sendMessage);
        } else if (Objects.equals(callbackData, CALLBACK_DATA_CANCEL)) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("Creación del alojamiento cancelado")
                    .build();
            messageList.add(sendMessage);
        }

        accommodationUserDataMap.remove(chatId);
    }

    @Override
    public boolean canHandle(String command) {
        return Objects.equals(command, "/crearalojamiento");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return accommodationUserDataMap.containsKey(chatId);
    }

    @Override
    public void removeUserData(long chatId) {
        accommodationUserDataMap.remove(chatId);
    }
}
