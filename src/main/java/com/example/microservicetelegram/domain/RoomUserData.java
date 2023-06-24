package com.example.microservicetelegram.domain;

import lombok.Data;

@Data
public class RoomUserData {
    private RoomStatus status;
    String accommodationId;
    private String name;
    private int maxPeople;
    private int quantity;
    private double price;

    public RoomUserData() {
        this.status = RoomStatus.START;
    }

    public enum RoomStatus {
        START,
        NAME,
        MAX_PEOPLE,
        QUANTITY,
        PRICE,
        CONFIRM
    }
}
