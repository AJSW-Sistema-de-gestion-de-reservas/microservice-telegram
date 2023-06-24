package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.domain.RoomUserData;
import com.example.microservicetelegram.dto.AccommodationDetailsResponseDto;
import com.example.microservicetelegram.services.AccommodationService;
import com.example.microservicetelegram.services.OwnerService;
import com.example.microservicetelegram.services.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Component
public class RoomCreationCommandHandler implements CommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomCreationCommandHandler.class);

    private static final String CALLBACK_DATA_CANCEL = "N";
    private static final String CALLBACK_DATA_CONFIRM = "Y";

    private final OwnerService ownerService;
    private final AccommodationService accommodationService;
    private final RoomService roomService;

    private final Map<Long, RoomUserData> roomUserDataMap;

    @Autowired
    public RoomCreationCommandHandler(OwnerService ownerService,
                                      AccommodationService accommodationService,
                                      RoomService roomService) {
        this.ownerService = ownerService;
        this.accommodationService = accommodationService;
        this.roomService = roomService;
        this.roomUserDataMap = new HashMap<>();
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage() && !update.hasCallbackQuery())
            return List.of();

        long chatId = (update.hasMessage()) ? update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();

        if (!roomUserDataMap.containsKey(chatId)) {
            if (!ownerService.existsByChatId(chatId)) {
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(chatId)
                        .text("Tenés que estar registrado como administrador para añadir habitaciones a tus " +
                                "alojamientos. Registrate con el comando /registroadmin")
                        .build();
                return List.of(sendMessage);
            }

            String accommodationId = update.getMessage().getText().split("_")[1];
            Optional<AccommodationDetailsResponseDto> accommodationInfo = accommodationService.getById(accommodationId);
            if (accommodationInfo.isEmpty()) {
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(chatId)
                        .text("El alojamiento no es válido. Podés ver tus alojamientos con el comando " +
                                "/misalojamientos")
                        .build();
                return List.of(sendMessage);
            }

            RoomUserData userData = new RoomUserData();
            userData.setAccommodationId(accommodationId);

            roomUserDataMap.put(chatId, userData);
        }

        List<SendMessage> messageList = new ArrayList<>();
        RoomUserData userData = roomUserDataMap.get(chatId);
        switch (userData.getStatus()) {
            case START -> handleStart(update, messageList, userData);
            case NAME -> handleName(update, messageList, userData);
            case MAX_PEOPLE -> handleMaxPeople(update, messageList, userData);
            case QUANTITY -> handleQuantity(update, messageList, userData);
            case PRICE -> handlePrice(update, messageList, userData);
            case CONFIRM -> handleConfirm(update, messageList, userData);
        }

        return messageList;
    }

    private void handleStart(Update update, List<SendMessage> messageList, RoomUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Por favor, ingrese el nombre de la habitación:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(RoomUserData.RoomStatus.NAME);
    }

    private void handleName(Update update, List<SendMessage> messageList, RoomUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        String name = update.getMessage().getText();
        userData.setName(name);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Por favor, ingrese la cantidad de personas máxima para la habitación:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(RoomUserData.RoomStatus.MAX_PEOPLE);
    }

    private void handleMaxPeople(Update update, List<SendMessage> messageList, RoomUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        int maxPeople;
        try {
            maxPeople = Integer.parseInt(update.getMessage().getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No ha ingresado un formato de número válido. Por favor, ingrese la cantidad de personas " +
                            "máxima para la habitación")
                    .build();
            messageList.add(sendMessage);
            return;
        }
        userData.setMaxPeople(maxPeople);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Por favor, ingresa la cantidad de habitaciones de este tipo que hay en el alojamiento:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(RoomUserData.RoomStatus.QUANTITY);
    }

    private void handleQuantity(Update update, List<SendMessage> messageList, RoomUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        int quantity;
        try {
            quantity = Integer.parseInt(update.getMessage().getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No ha ingresado un formato de número válido. Por favor, ingrese la cantidad de habitaciones " +
                            "de este tipo que hay en el alojamiento:")
                    .build();
            messageList.add(sendMessage);
            return;
        }
        userData.setQuantity(quantity);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Por favor, ingresa el precio por día de la habitación:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(RoomUserData.RoomStatus.PRICE);
    }

    private void handlePrice(Update update, List<SendMessage> messageList, RoomUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        double price;
        try {
            price = Double.parseDouble(update.getMessage().getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No ha ingresado un formato de número válido. Por favor, ingrese el precio de la habitación:")
                    .build();
            messageList.add(sendMessage);
            return;
        }
        userData.setPrice(price);

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
                        Cantidad máxima de personas: %d
                        Cantidad de habitaciones: %d
                        Precio por día: %.2f
                        """.formatted(userData.getName(), userData.getMaxPeople(), userData.getQuantity(),
                        userData.getPrice()))
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(keyboard).build())
                .build();
        messageList.add(sendMessage);

        userData.setStatus(RoomUserData.RoomStatus.CONFIRM);
    }

    private void handleConfirm(Update update, List<SendMessage> messageList, RoomUserData userData) {
        if (!update.hasCallbackQuery())
            return;

        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (Objects.equals(callbackData, CALLBACK_DATA_CONFIRM)) {
            LOGGER.info(userData.toString());

            boolean result = roomService.create(
                    chatId,
                    userData.getAccommodationId(),
                    userData.getName(),
                    userData.getMaxPeople(),
                    userData.getQuantity(),
                    userData.getPrice()
            );

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text((result) ? "Habitación creada con éxito! Podés ver todas las habitaciones de este " +
                            "alojamiento con el comando /mishabitaciones_%s".formatted(userData.getAccommodationId())
                            : "Ha ocurrido un error al crear la habitación")
                    .build();
            messageList.add(sendMessage);
        } else if (Objects.equals(callbackData, CALLBACK_DATA_CANCEL)) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("Creación de la habitación cancelada")
                    .build();
            messageList.add(sendMessage);
        }

        roomUserDataMap.remove(chatId);
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("/crearhabitacion_");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return roomUserDataMap.containsKey(chatId);
    }

    @Override
    public void removeUserData(long chatId) {
        roomUserDataMap.remove(chatId);
    }
}
