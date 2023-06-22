package com.example.microservicetelegram.bot;

import com.example.microservicetelegram.handlers.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBot.class);

    private final List<CommandHandler> commands;
    private final String botUsername;

    public TelegramBot(@Value("${telegram.bot.username}") String botUsername,
                       @Value("${telegram.bot.token}") String botToken,
                       TelegramBotsApi telegramBotsApi,
                       List<CommandHandler> commands) throws TelegramApiException {
        super(new DefaultBotOptions(), botToken);
        this.botUsername = botUsername;
        this.commands = commands;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage())
            LOGGER.info("Update has no message");

        String text;
        Long chatId;
        if (update.hasMessage()) {
            text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();

            LOGGER.info(update.getMessage().getText());
        } else if (update.hasCallbackQuery()) {
            text = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();

            LOGGER.info(update.getCallbackQuery().getData());
        } else {
            text = "";
            chatId = 0L;

            LOGGER.info("Message has no text");
        }

        String commandText = (text.indexOf(' ') != -1) ? text.substring(0, text.indexOf(' ')) : text;
        Optional<CommandHandler> command = commands.stream()
                .filter(c -> c.canHandle(commandText) || c.hasUserData(chatId))
                .findFirst();

        if (command.isPresent()) {
            List<SendMessage> messageList = command.get().handle(update);
            messageList.forEach(s -> {
                try {
                    execute(s);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });

            command.stream()
                    .filter(c -> c != command.get())
                    .forEach(c -> c.removeUserData(chatId));
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
