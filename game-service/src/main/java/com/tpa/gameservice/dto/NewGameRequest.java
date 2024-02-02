package com.tpa.gameservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewGameRequest {
    String firstPlayerUsername;
    String secondPlayerUsername;
}
