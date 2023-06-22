package com.example.microservicetelegram.config;

import com.example.microservicetelegram.handlers.ClientInfoCommandHandler;
import com.example.microservicetelegram.handlers.CommandHandler;
import com.example.microservicetelegram.handlers.RegistrationCommandHandler;
import com.example.microservicetelegram.handlers.SearchCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class TelegramConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public List<CommandHandler> commandsMap(RegistrationCommandHandler registration,
                                            SearchCommandHandler search,
                                            ClientInfoCommandHandler clientInfo) {
        List<CommandHandler> commands = new ArrayList<>();
        commands.add(registration);
        commands.add(search);
        commands.add(clientInfo);
        return commands;
    }

}
