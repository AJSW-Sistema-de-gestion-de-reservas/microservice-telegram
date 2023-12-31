package com.example.microservicetelegram.config;

import com.example.microservicetelegram.handlers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
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
    public List<CommandHandler> commandsMap(RegistrationClientCommandHandler registrationClient,
                                            RegistrationOwnerCommandHandler registrationOwner,
                                            SearchCommandHandler search,
                                            AccommodationDetailsCommandHandler accommodationDetails,
                                            ClientInfoCommandHandler clientInfo,
                                            BookingClientInfoCommandHandler bookingInfo,
                                            BookingCreationCommandHandler bookingCreation,
                                            AccommodationCreationCommandHandler accommodationCreation,
                                            AccommodationOwnerInfoCommandHandler accommodationOwnerInfo,
                                            RoomOwnerInfoCommandHandler roomOwnerInfo,
                                            BookingAccommodationOwnerInfoCommandHandler bookingAccommodationOwner,
                                            CancelCommandHandler cancelCommandHandler,
                                            HelpInfoCommandHandler helpInfoCommandHandler) {
        List<CommandHandler> commands = new ArrayList<>();
        commands.add(registrationClient);
        commands.add(registrationOwner);
        commands.add(search);
        commands.add(clientInfo);
        commands.add(accommodationDetails);
        commands.add(bookingInfo);
        commands.add(bookingCreation);
        commands.add(accommodationCreation);
        commands.add(accommodationOwnerInfo);
        commands.add(roomOwnerInfo);
        commands.add(bookingAccommodationOwner);
        commands.add(cancelCommandHandler);
        commands.add(helpInfoCommandHandler);
        return commands;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
