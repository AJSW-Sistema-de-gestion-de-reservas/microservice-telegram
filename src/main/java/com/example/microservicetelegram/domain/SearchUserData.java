package com.example.microservicetelegram.domain;

import lombok.Data;

@Data
public class SearchUserData {
    private SearchStatus status;
    private String city;

    public SearchUserData() {
        this.status = SearchStatus.START;
    }

    public enum SearchStatus {
        START,
        CITY,
        SHOW_RESULTS
    }
}
