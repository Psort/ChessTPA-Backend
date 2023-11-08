package com.tpa.gameservice.dto;

import lombok.Getter;

@Getter
public class NewGameRequest {
    String firstPlayerUsername;
    String secondPlayerUsername;
}
