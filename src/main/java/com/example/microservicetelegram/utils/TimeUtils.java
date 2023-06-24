package com.example.microservicetelegram.utils;

import java.time.Instant;
import java.util.Date;

public class TimeUtils {
    public static Instant convertInstantDateToUTC(Date date) {
        Instant instant = date.toInstant();
        return Instant.parse(instant.toString().substring(0, instant.toString().indexOf("T")) + "T00:00:00Z");
    }
}
