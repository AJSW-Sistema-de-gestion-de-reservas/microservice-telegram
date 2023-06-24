package com.example.microservicetelegram.domain;

import lombok.Data;

import java.util.Date;

@Data
public class BookingUserData {
    private BookingStatus status;
    private String accommodationId;
    private String roomId;
    private Date startDate;
    private Date endDate;

    public BookingUserData() {
        this.status = BookingStatus.START;
    }

    public enum BookingStatus {
        START,
        START_DATE,
        END_DATE,
        CONFIRM
    }
}
