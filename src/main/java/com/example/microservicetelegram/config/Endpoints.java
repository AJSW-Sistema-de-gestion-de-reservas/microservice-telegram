package com.example.microservicetelegram.config;

public class Endpoints {
    private static final String API_BASE_URL = "http://localhost:8080/api";

    public static final String API_CLIENT = API_BASE_URL + "/client";
    public static final String API_CLIENT_REGISTER = API_CLIENT;
    public static final String API_CLIENT_INFO_FROM_CHAT_ID = API_CLIENT + "/chatId=";

    public static final String API_OWNER = API_BASE_URL + "/owner";
    public static final String API_OWNER_REGISTER = API_OWNER;
    public static final String API_OWNER_INFO_FROM_CHAT_ID = API_OWNER + "/chatId=";

    public static final String API_ACCOMMODATION = API_BASE_URL + "/accommodation";
    public static final String API_ACCOMMODATION_CREATE = API_ACCOMMODATION;
    public static final String API_ACCOMMODATION_BY_ID = API_ACCOMMODATION + "/{accommodationId}";
    public static final String API_ACCOMMODATION_SEARCH = API_ACCOMMODATION + "/search";
    public static final String API_ACCOMMODATION_SEARCH_OWNER = API_ACCOMMODATION + "/search/owner={ownerId}";

    public static final String API_ROOM = API_ACCOMMODATION_BY_ID + "/room";
    public static final String API_ROOM_BY_ID = API_ROOM + "/";

    public static final String API_BOOKING_CREATE = API_BASE_URL + "/accommodation/{accommodationId}/room/{roomId}/booking";
    public static final String API_BOOKING_CLIENT = API_BASE_URL + "/client/{clientId}/booking";
    public static final String API_BOOKING_ACCOMMODATION = API_BASE_URL + "/accommodation/{accommodationId}/booking";
    public static final String API_BOOKING_ACCOMMODATION_DATE = "/accommodation/{accommodationId}/booking/{date}";
    public static final String API_BOOKING_ACCOMMODATION_DATE_BETWEEN = "/accommodation/{accommodationId}/booking/{startDate}/{endDate}";

    public static final String API_ROOM= API_ACCOMMODATION_BY_ID + "/room";
    public static final String API_ROOM_BY_ID= API_ROOM+ "/";
}
