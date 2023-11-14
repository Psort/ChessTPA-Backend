package com.tpa.userservice.dto;

import lombok.Getter;

@Getter
public class NewGameRequest {
    private String gameId;
    private String firstPlayerUsername;
    private String secondPlayerUsername;
}
