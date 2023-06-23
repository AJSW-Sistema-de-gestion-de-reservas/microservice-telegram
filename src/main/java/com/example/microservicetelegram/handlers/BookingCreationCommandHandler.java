package com.example.microservicetelegram.handlers;

import com.example.microservicetelegram.domain.BookingUserData;
import com.example.microservicetelegram.services.BookingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class BookingCreationCommandHandler implements CommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingCreationCommandHandler.class);

    private static final String CALLBACK_DATA_CANCEL = "N";
    private static final String CALLBACK_DATA_CONFIRM = "Y";

    private final BookingService bookingService;

    private final Map<Long, BookingUserData> bookingUserDataMap;

    public BookingCreationCommandHandler(BookingService bookingService) {
        this.bookingService = bookingService;
        this.bookingUserDataMap = new HashMap<>();
    }

    @Override
    public List<SendMessage> handle(Update update) {
        if (!update.hasMessage() && !update.hasCallbackQuery())
            return List.of();

        long chatId = (update.hasMessage()) ? update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();

        if (!bookingUserDataMap.containsKey(chatId))
            bookingUserDataMap.put(chatId, new BookingUserData());

        List<SendMessage> messageList = new ArrayList<>();
        BookingUserData userData = bookingUserDataMap.get(chatId);
        switch (userData.getStatus()) {
            case START -> handleStart(update, messageList, userData);
            case START_DATE -> handleStartDate(update, messageList, userData);
            case END_DATE -> handleEndDate(update, messageList, userData);
            case CONFIRM -> handleConfirm(update, messageList, userData);
        }

        return messageList;
    }

    private void handleStart(Update update, List<SendMessage> messageList, BookingUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (!(StringUtils.countMatches(messageText, "_") == 2)) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("El comando enviado no es válido")
                    .build();

            messageList.add(sendMessage);
            removeUserData(chatId);
            return;
        }

        String[] split = messageText.split("_");
        String accommodationId = split[1];
        String roomId = split[2];

        userData.setAccommodationId(accommodationId);
        userData.setRoomId(roomId);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Por favor, ingresa la fecha de entrada en formato dd/MM/yyyy: ")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(BookingUserData.BookingStatus.START_DATE);
    }

    private void handleStartDate(Update update, List<SendMessage> messageList, BookingUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate;
        try {
            startDate = dateFormat.parse(messageText);
        } catch (ParseException e) {
            e.printStackTrace();

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No se ingresó una fecha de entrada válida. Intente ingresar nuevamente una fecha de " +
                            "entrada en formato dd/MM/yyyy")
                    .build();
            messageList.add(sendMessage);
            return;
        }

        userData.setStartDate(startDate);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Por favor, ingresa la fecha de salida en formato dd/MM/yyyy:")
                .build();
        messageList.add(sendMessage);

        userData.setStatus(BookingUserData.BookingStatus.END_DATE);
    }

    private void handleEndDate(Update update, List<SendMessage> messageList, BookingUserData userData) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date endDate;
        try {
            endDate = dateFormat.parse(messageText);
        } catch (ParseException e) {
            e.printStackTrace();

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("No se ingresó una fecha de salida válida. Intente ingresar nuevamente una fecha de " +
                            "salida en formato dd/MM/yyyy")
                    .build();
            messageList.add(sendMessage);
            return;
        }

        if (endDate.before(userData.getStartDate())) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("La fecha de salida no puede ser anterior a la fecha de entrada. Intente ingresar " +
                            "nuevamente una fecha de salida en formato dd/MM/yyyy")
                    .build();
            messageList.add(sendMessage);
            return;
        }

        userData.setEndDate(endDate);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("Cancel")
                .callbackData(CALLBACK_DATA_CANCEL)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text("Confirm")
                .callbackData(CALLBACK_DATA_CONFIRM)
                .build());
        keyboard.add(row);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("""
                        Confirma los datos de la reserva:
                                                
                        Fecha de entrada: %s
                        Fecha de salida: %s
                        """.formatted(dateFormat.format(userData.getStartDate()), dateFormat.format(userData.getEndDate())))
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(keyboard).build())
                .build();
        messageList.add(sendMessage);

        userData.setStatus(BookingUserData.BookingStatus.CONFIRM);
    }

    private void handleConfirm(Update update, List<SendMessage> messageList, BookingUserData userData) {
        if (!update.hasCallbackQuery())
            return;

        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (Objects.equals(callbackData, CALLBACK_DATA_CONFIRM)) {
            LOGGER.info(userData.toString());

            boolean result = bookingService.book(
                    chatId,
                    userData.getAccommodationId(),
                    userData.getRoomId(),
                    userData.getStartDate(),
                    userData.getEndDate()
            );

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text((result) ? "Reserva completada con éxito!" : "Ha ocurrido un error en la reserva")
                    .build();

            messageList.add(sendMessage);
        } else if (Objects.equals(callbackData, CALLBACK_DATA_CANCEL)) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("Reserva cancelada")
                    .build();
            messageList.add(sendMessage);
        }

        bookingUserDataMap.remove(chatId);
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("/reservar");
    }

    @Override
    public boolean hasUserData(long chatId) {
        return bookingUserDataMap.containsKey(chatId);
    }

    @Override
    public void removeUserData(long chatId) {
        bookingUserDataMap.remove(chatId);
    }
}
