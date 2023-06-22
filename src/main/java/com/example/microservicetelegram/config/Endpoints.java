package com.example.microservicetelegram.config;

public class Endpoints {
    private static final String API_BASE_URL = "http://localhost:8080";

    public static final String API_CLIENT_REGISTER = API_BASE_URL + "/api/client";
    public static final String API_CLIENT_INFO_FROM_CHAT_ID = API_BASE_URL + "/api/client/chatId=";
    public static final String API_ACCOMMODATION_SEARCH = API_BASE_URL + "/api/accommodation/search";
}
